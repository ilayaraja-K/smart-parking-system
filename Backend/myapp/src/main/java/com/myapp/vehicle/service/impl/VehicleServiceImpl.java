package com.myapp.vehicle.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.myapp.auth.entity.User;
import com.myapp.auth.repository.UserRepository;
import com.myapp.exception.CustomException;
import com.myapp.security.jwt.JwtUtil;
import com.myapp.vehicle.dto.VehicleUserResponse;
import com.myapp.vehicle.entity.UserVehicle;
import com.myapp.vehicle.entity.Vehicle;
import com.myapp.vehicle.repository.UserVehicleRepository;
import com.myapp.vehicle.repository.VehicleRepository;
import com.myapp.vehicle.service.VehicleService;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/**
 * Enterprise implementation of VehicleService
 */
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserVehicleRepository userVehicleRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // ================= HELPER METHODS =================

    private User getCurrentUser() {
        String email = jwtUtil.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
    }

    private boolean isAdmin(User user) {
        return "ADMIN".equalsIgnoreCase(user.getRole());
    }

    private String normalizeVehicleNumber(String number) {
        return number.toUpperCase().replaceAll("\\s+", "");
    }

    // ================= SERVICE METHODS =================

    /**
     * Register vehicle (create or map)
     */
    @Override
    public Vehicle registerVehicle(Vehicle vehicle) {

        User currentUser = getCurrentUser();

        // 🔥 Normalize vehicle number
        String normalizedNumber = normalizeVehicleNumber(vehicle.getVehicleNumber());

        // 🔍 Check if vehicle already exists
        Vehicle existingVehicle = vehicleRepository
                .findByVehicleNumber(normalizedNumber)
                .orElse(null);

        Vehicle finalVehicle;

        if (existingVehicle == null) {
            // ✅ Create new vehicle
            vehicle.setVehicleNumber(normalizedNumber);
            vehicle.setCreatedOn(LocalDateTime.now());
            vehicle.setCreatedBy(currentUser.getId());

            finalVehicle = vehicleRepository.save(vehicle);
        } else {
            // 🔁 Reuse existing vehicle
            finalVehicle = existingVehicle;
        }

        // 🔐 Check if mapping already exists
        boolean mappingExists = userVehicleRepository
                .existsByUserIdAndVehicleId(currentUser.getId(), finalVehicle.getId());

        if (mappingExists) {
            throw new CustomException("Vehicle already registered for this user");
        }

        // 🔗 Create mapping
        UserVehicle mapping = UserVehicle.builder()
                .userId(currentUser.getId())
                .vehicleId(finalVehicle.getId())
                .ownershipType(existingVehicle == null ? "OWNER" : "FAMILY")
                .createdOn(LocalDateTime.now())
                .createdBy(currentUser.getId())
                .build();

        userVehicleRepository.save(mapping);

        return finalVehicle;
    }

    /**
     * Get my vehicles
     */
    @Override
    public List<Vehicle> getMyVehicles() {

        User currentUser = getCurrentUser();

        List<UserVehicle> mappings = userVehicleRepository.findByUserId(currentUser.getId());

        return mappings.stream()
                .map(mapping -> vehicleRepository.findById(mapping.getVehicleId())
                        .orElseThrow(() -> new CustomException("Vehicle not found")))
                .collect(Collectors.toList());
    }

    /**
     * Get my vehicles by type
     */
    @Override
    public List<Vehicle> getMyVehiclesByType(String type) {

        User currentUser = getCurrentUser();

        Long userId = currentUser.getId();

        return userVehicleRepository.findVehiclesByUserIdAndType(userId, type);
    }

    /**
     * Get vehicle by ID
     */
    @Override
    public Vehicle getVehicleById(Long vehicleId) {

        User currentUser = getCurrentUser();

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new CustomException("Vehicle not found"));

        // ADMIN → allowed
        if (isAdmin(currentUser)) {
            return vehicle;
        }

        // USER → must have mapping
        boolean exists = userVehicleRepository
                .existsByUserIdAndVehicleId(currentUser.getId(), vehicleId);

        if (!exists) {
            throw new CustomException("Access denied: Not your vehicle");
        }

        return vehicle;
    }

    /**
     * Remove vehicle (delete mapping only)
     */
    @Override
    @Transactional
    public void removeVehicle(Long vehicleId) {

        User currentUser = getCurrentUser();

        // ADMIN → delete all mappings for this vehicle (optional logic)
        if (isAdmin(currentUser)) {
            userVehicleRepository.findByVehicleId(vehicleId)
                    .forEach(mapping -> userVehicleRepository.delete(mapping));
            return;
        }

        // USER → delete only own mapping
        boolean exists = userVehicleRepository
                .existsByUserIdAndVehicleId(currentUser.getId(), vehicleId);
        

        if (!exists) {
            throw new CustomException("Access denied: Cannot remove this vehicle");
        }

        userVehicleRepository.deleteByUserIdAndVehicleId(currentUser.getId(), vehicleId);
    }

    /**
     * ADMIN: Get all vehicles
     */
    @Override
    public List<VehicleUserResponse> getAllVehicles() {

        User currentUser = getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new CustomException("Access denied: Admin only");
        }

        List<UserVehicle> mappings = userVehicleRepository.findAll();

        return mappings.stream().map(mapping -> {

            Vehicle vehicle = vehicleRepository.findById(mapping.getVehicleId())
                    .orElseThrow(() -> new CustomException("Vehicle not found"));

            return VehicleUserResponse.builder()
                    .vehicleId(vehicle.getId())
                    .vehicleNumber(vehicle.getVehicleNumber())
                    .vehicleType(vehicle.getVehicleType())
                    .model(vehicle.getModel())
                    .userId(mapping.getUserId())
                    .ownershipType(mapping.getOwnershipType())
                    .build();

        }).toList();
    }
    /**
     * ADMIN: Get vehicles by userId
     */
    @Override
    public List<Vehicle> getVehiclesByUserId(Long userId) {

        User currentUser = getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new CustomException("Access denied: Admin only");
        }

        List<UserVehicle> mappings = userVehicleRepository.findByUserId(userId);

        return mappings.stream()
                .map(mapping -> vehicleRepository.findById(mapping.getVehicleId())
                        .orElseThrow(() -> new CustomException("Vehicle not found")))
                .collect(Collectors.toList());
    }

    /**
     * ADMIN: Get users by vehicleId
     */
    @Override
    public List<Long> getUsersByVehicleId(Long vehicleId) {

        User currentUser = getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new CustomException("Access denied: Admin only");
        }

        return userVehicleRepository.findByVehicleId(vehicleId)
                .stream()
                .map(UserVehicle::getUserId)
                .collect(Collectors.toList());
    }
    
    /*update the vehicle details*/
    @Override
    public Vehicle updateVehicle(Long vehicleId, Vehicle updatedVehicle) {

        User currentUser = getCurrentUser();

        Vehicle existingVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new CustomException("Vehicle not found"));

        // 🔐 ADMIN → allowed
        if (!isAdmin(currentUser)) {

            // 🔥 Fetch ALL mappings for this vehicle
            List<UserVehicle> mappings =
                    userVehicleRepository.findByVehicleId(vehicleId);

            // 🔥 Find mapping for current user
            UserVehicle userMapping = mappings.stream()
                    .filter(m -> m.getUserId().equals(currentUser.getId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new CustomException("Access denied: Not your vehicle")
                    );

            // 🔥 STRICT OWNER CHECK
            if (!"OWNER".equalsIgnoreCase(
                    userMapping.getOwnershipType() == null ? "" : userMapping.getOwnershipType().trim()
            )) {
                throw new CustomException("Only OWNER can update vehicle");
            }
        }

        // 🔥 Update fields
        if (updatedVehicle.getVehicleType() != null) {
            existingVehicle.setVehicleType(updatedVehicle.getVehicleType());
        }

        if (updatedVehicle.getModel() != null) {
            existingVehicle.setModel(updatedVehicle.getModel());
        }

        existingVehicle.setUpdatedOn(LocalDateTime.now());
        existingVehicle.setUpdatedBy(currentUser.getId());

        return vehicleRepository.save(existingVehicle);
    }
    
    
    @Override
    public List<Vehicle> getVehiclesByUserIdSecure(Long userId) {

        User currentUser = getCurrentUser();

        // 🔐 SECURITY CHECK
        if (!isAdmin(currentUser) && !currentUser.getId().equals(userId)) {
            throw new CustomException("Access denied");
        }

        List<UserVehicle> mappings = userVehicleRepository.findByUserId(userId);

        return mappings.stream()
                .map(mapping -> vehicleRepository.findById(mapping.getVehicleId())
                        .orElseThrow(() -> new CustomException("Vehicle not found")))
                .toList();
    }
    
    
}