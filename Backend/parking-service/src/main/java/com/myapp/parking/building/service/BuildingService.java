package com.myapp.parking.building.service;

import java.util.List;

import com.myapp.parking.building.entity.Building;

/**
 * Service interface for Building operations
 */
public interface BuildingService {

    /**
     * Create a new building
     * - Only ADMIN allowed
     * - Sets audit fields
     */
    Building createBuilding(Building building);

    /**
     * Get all buildings
     * - Public access (any logged-in user)
     */
    List<Building> getAllBuildings();

    /**
     * Get buildings by city
     */
    List<Building> getBuildingsByCity(String city);
}