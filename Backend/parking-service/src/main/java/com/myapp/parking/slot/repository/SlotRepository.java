package com.myapp.parking.slot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myapp.parking.slot.entity.Slot;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

    List<Slot> findByBuildingId(Long buildingId);

    List<Slot> findByBuildingIdAndVehicleType(Long buildingId, String vehicleType);

    List<Slot> findByBuildingIdAndVehicleTypeAndStatus(
            Long buildingId,
            String vehicleType,
            String status
    );

    boolean existsBySlotIdAndStatus(Long slotId, String status);
}