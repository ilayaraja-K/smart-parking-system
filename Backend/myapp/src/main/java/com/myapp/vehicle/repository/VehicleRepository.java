package com.myapp.vehicle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myapp.vehicle.entity.Vehicle;

/**
 * Repository for Vehicle (Master Table)
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Find vehicle by normalized vehicle number
     */
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    /**
     * Check if vehicle exists by number
     */
    boolean existsByVehicleNumber(String vehicleNumber);
}