package com.myapp.parking.booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myapp.parking.booking.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 🔐 User active booking check
    Optional<Booking> findByUserIdAndStatus(Long userId, String status);

    // 🔐 Get bookings by user
    List<Booking> findByUserId(Long userId);

    // 🔐 Slot already booked
    boolean existsBySlotIdAndStatus(Long slotId, String status);

    // 🔥 ADD THIS (YOUR FIX)
    boolean existsByVehicleIdAndStatus(Long vehicleId, String status);
    
    List<Booking> findByStatus(String status);
}