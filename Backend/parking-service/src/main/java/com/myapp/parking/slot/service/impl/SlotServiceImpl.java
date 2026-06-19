package com.myapp.parking.slot.service.impl;

import java.time.LocalDateTime;

import java.util.List;

import org.springframework.stereotype.Service;

import com.myapp.parking.building.repository.BuildingRepository;
import com.myapp.parking.booking.repository.BookingRepository;
import com.myapp.parking.exception.CustomException;
import com.myapp.parking.security.jwt.JwtUtil;
import com.myapp.parking.slot.entity.Slot;
import com.myapp.parking.slot.repository.SlotRepository;
import com.myapp.parking.slot.service.SlotService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final JwtUtil jwtUtil;
    private final BuildingRepository buildingRepository;
    private final BookingRepository bookingRepository;

    private Long getCurrentUserId() {
        return jwtUtil.getCurrentUserId();
    }

    private String getCurrentUserRole() {
        return jwtUtil.getCurrentUserRole();
    }

    private boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(getCurrentUserRole());
    }

    @Override
    public Slot createSlot(Slot slot) {

        // 🔐 ADMIN ONLY
        if (!isAdmin()) {
            throw new CustomException("Access denied: Admin only");
        }

        // 🔥 VALIDATION
        if (slot.getBuildingId() == null) {
            throw new CustomException("Building ID is required");
        }

        buildingRepository.findById(slot.getBuildingId())
                .orElseThrow(() -> new CustomException("Building not found"));

        if (slot.getVehicleType() == null || slot.getVehicleType().isEmpty()) {
            throw new CustomException("Vehicle type is required");
        }

        if (slot.getSlotNumber() == null || slot.getSlotNumber().isEmpty()) {
            throw new CustomException("Slot number is required");
        }

        // 🔥 Normalize
        slot.setVehicleType(slot.getVehicleType().toUpperCase());

        // 🔥 Default
        slot.setStatus("AVAILABLE");

        // 🔐 Audit
        slot.setCreatedBy(getCurrentUserId());
        slot.setCreatedOn(LocalDateTime.now());

        return slotRepository.save(slot);
    }

    @Override
    public List<Slot> getAllSlots() {
        return slotRepository.findAll();
    }

    @Override
    public List<Slot> getSlotsByBuilding(Long buildingId) {

        if (buildingId == null) {
            throw new CustomException("Building ID is required");
        }

        return slotRepository.findByBuildingId(buildingId);
    }

    @Override
    public List<Slot> getAvailableSlots(Long buildingId, String vehicleType) {

        if (buildingId == null) {
            throw new CustomException("Building ID is required");
        }

        if (vehicleType == null || vehicleType.isEmpty()) {
            throw new CustomException("Vehicle type is required");
        }

        return slotRepository.findByBuildingIdAndVehicleTypeAndStatus(
                buildingId,
                vehicleType.toUpperCase(),
                "AVAILABLE"
        );
    }
    

    @Override
    public void deleteSlot(Long slotId) {

        // 🔐 ADMIN ONLY
        if (!isAdmin()) {
            throw new CustomException("Access denied: Admin only");
        }

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new CustomException("Slot not found"));

        // 🔐 Block deletion if slot has an ACTIVE booking
        if (bookingRepository.existsBySlotIdAndStatus(slotId, "ACTIVE")) {
            throw new CustomException("Cannot delete slot: it has an active booking");
        }

        slotRepository.delete(slot);
    }
}