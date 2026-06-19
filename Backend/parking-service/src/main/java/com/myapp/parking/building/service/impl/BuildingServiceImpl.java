package com.myapp.parking.building.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.myapp.parking.building.entity.Building;
import com.myapp.parking.building.repository.BuildingRepository;
import com.myapp.parking.building.service.BuildingService;
import com.myapp.parking.exception.CustomException;
import com.myapp.parking.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final JwtUtil jwtUtil;

    // 🔐 Helpers
    private Long getCurrentUserId() {
        return jwtUtil.getCurrentUserId();
    }

    private String getCurrentUserRole() {
        return jwtUtil.getCurrentUserRole();
    }

    private boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(getCurrentUserRole());
    }

    // ================= METHODS =================
    
    /*Create building*/

    @Override
    public Building createBuilding(Building building) {

        // 🔐 ADMIN ONLY
        if (!isAdmin()) {
            throw new CustomException("Access denied: Admin only");
        }

        // Validation
        if (building.getName() == null || building.getName().trim().isEmpty()) {
            throw new CustomException("Building name is required");
        }

        // Audit
        building.setCreatedBy(getCurrentUserId());
        building.setCreatedOn(LocalDateTime.now());

        return buildingRepository.save(building);
    }
    
    /*get all the building */

    @Override
    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    /*get building by city*/
    @Override
    public List<Building> getBuildingsByCity(String city) {

        if (city == null || city.trim().isEmpty()) {
            throw new CustomException("City cannot be empty");
        }

        List<Building> buildings = buildingRepository.findByCityIgnoreCase(city);

        if (buildings.isEmpty()) {
            throw new CustomException("No buildings found in city: " + city);
        }

        return buildings;
    }
}