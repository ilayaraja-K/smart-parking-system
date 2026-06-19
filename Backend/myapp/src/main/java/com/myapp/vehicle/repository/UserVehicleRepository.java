package com.myapp.vehicle.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.myapp.vehicle.entity.UserVehicle;
import com.myapp.vehicle.entity.Vehicle;

/**
 * Repository for User-Vehicle mapping
 */
@Repository
public interface UserVehicleRepository extends JpaRepository<UserVehicle, Long> {

    /**
     * Get all vehicle mappings for a user
     */
    List<UserVehicle> findByUserId(Long userId);

    /**
     * Get mapping by user and vehicle
     */
    Optional<UserVehicle> findByUserIdAndVehicleId(Long userId, Long vehicleId);

    /**
     * Check if mapping exists (ownership validation)
     */
    boolean existsByUserIdAndVehicleId(Long userId, Long vehicleId);

    /**
     * Delete mapping (used when user removes vehicle)
     */
    void deleteByUserIdAndVehicleId(Long userId, Long vehicleId);

    /**
     * Admin: get all users for a vehicle
     */
    List<UserVehicle> findByVehicleId(Long vehicleId);
    
    
    @Query("""
    	    SELECT v FROM UserVehicle uv
    	    JOIN Vehicle v ON uv.vehicleId = v.id
    	    WHERE uv.userId = :userId AND v.vehicleType = :type
    	""")
    	List<Vehicle> findVehiclesByUserIdAndType(Long userId, String type);
}