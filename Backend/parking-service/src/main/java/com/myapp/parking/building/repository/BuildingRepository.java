package com.myapp.parking.building.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myapp.parking.building.entity.Building;

/**
 * Repository for Building entity
 */
@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    /**
     * Find buildings by city
     */
    List<Building> findByCityIgnoreCase(String city);
}