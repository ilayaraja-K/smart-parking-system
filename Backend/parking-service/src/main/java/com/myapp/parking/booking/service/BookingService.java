package com.myapp.parking.booking.service;

import java.util.List;

import com.myapp.parking.booking.entity.Booking;

/**
 * Service interface for Booking operations
 */
public interface BookingService {

    /**
     * Create a booking
     * - Validate slot availability
     * - Validate vehicle ownership via Vehicle Service
     * - Prevent double booking
     * - Lock slot (OCCUPIED)
     * - Set startTime
     */
    Booking createBooking(Long vehicleId, Long slotId);

    /**
     * Get bookings of current user
     */
    List<Booking> getMyBookings();

    /**
     * Complete booking (user exit)
     * - Set endTime
     * - Calculate duration (future)
     * - Mark status COMPLETED
     * - Release slot (AVAILABLE)
     */
    Booking completeBooking(Long bookingId);

    /**
     * Cancel booking (optional)
     * - Only if not used
     * - Release slot
     */
    Booking cancelBooking(Long bookingId);
    
    List<Booking> getBookingsByUserId(Long userId);
    List<Booking> getAllBookings();
    List<Booking> getBookingsByStatus(String status);
}