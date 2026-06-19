package com.myapp.vehicle.service;

import java.util.List;

import com.myapp.vehicle.dto.VehicleUserResponse;
import com.myapp.vehicle.entity.Vehicle;

/**
 * Service interface for enterprise vehicle module
 */
public interface VehicleService {

    /**
     * Register vehicle (create or map)
     * - Normalize vehicle number
     * - Reuse existing vehicle OR create new
     * - Create user-vehicle mapping
     */
    Vehicle registerVehicle(Vehicle vehicle);

    /**
     * Get logged-in user's vehicles
     */
    List<Vehicle> getMyVehicles();

    /**
     * Get logged-in user's vehicles by type (CAR / BIKE)
     */
    List<Vehicle> getMyVehiclesByType(String type);

    /**
     * Get vehicle by ID
     * - USER → only if mapped
     * - ADMIN → any vehicle
     */
    Vehicle getVehicleById(Long vehicleId);

    /**
     * Remove vehicle mapping (NOT delete vehicle globally)
     * - USER → only own mapping
     * - ADMIN → can remove any mapping
     */
    void removeVehicle(Long vehicleId);

    /**
     * ADMIN: Get all vehicles
     */
    public List<VehicleUserResponse> getAllVehicles();

    /**
     * ADMIN: Get vehicles by userId
     */
    List<Vehicle> getVehiclesByUserId(Long userId);

    /**
     * ADMIN: Get users using a vehicle (for debugging / analytics)
     */
    List<Long> getUsersByVehicleId(Long vehicleId);
    
    
    Vehicle updateVehicle(Long vehicleId, Vehicle vehicle);
    
    List<Vehicle> getVehiclesByUserIdSecure(Long userId);
    
    
}