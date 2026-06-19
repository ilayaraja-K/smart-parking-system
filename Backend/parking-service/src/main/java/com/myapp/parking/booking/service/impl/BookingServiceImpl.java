package com.myapp.parking.booking.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.myapp.parking.booking.dto.AppResponseWrapper;
import com.myapp.parking.booking.dto.VehicleResponse;
import com.myapp.parking.booking.entity.Booking;
import com.myapp.parking.booking.repository.BookingRepository;
import com.myapp.parking.booking.service.BookingService;
import com.myapp.parking.exception.CustomException;
import com.myapp.parking.security.jwt.JwtUtil;
import com.myapp.parking.slot.entity.Slot;
import com.myapp.parking.slot.repository.SlotRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of BookingService
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final JwtUtil jwtUtil;
    private final WebClient webClient;

    // ================= HELPER METHODS =================

    private Long getCurrentUserId() {
        return jwtUtil.getCurrentUserId();
    }

    /**
     * Validate vehicle via Vehicle Service
     */
    protected VehicleResponse validateVehicle(Long vehicleId) {
        try {
            AppResponseWrapper<VehicleResponse> response = webClient.get()
                    .uri("http://localhost:8090/uvmgmt/vehicles/{id}", vehicleId)
                    .header("Authorization", "Bearer " + jwtUtil.getCurrentToken())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<AppResponseWrapper<VehicleResponse>>() {})
                    .block();

            if (response == null || response.getAppResponse() == null) {
                throw new CustomException("Vehicle not found");
            }

            return response.getAppResponse();

        } catch (Exception e) {
            throw new CustomException("Invalid vehicle or not accessible");
        }
    }
    // ================= SERVICE METHODS =================

    /**
     * Create booking
     */
    @Override
    @Transactional
    public Booking createBooking(Long vehicleId, Long slotId) {

        Long userId = getCurrentUserId();

        // 🔐 Validate vehicle
        VehicleResponse vehicle = validateVehicle(vehicleId);

        // 🔐 Check active booking
        if (bookingRepository.findByUserIdAndStatus(userId, "ACTIVE").isPresent()) {
            throw new CustomException("You already have an active booking");
        }

        // 🔐 Slot check
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new CustomException("Slot not found"));

        if (!"AVAILABLE".equalsIgnoreCase(slot.getStatus())) {
            throw new CustomException("Slot is not available");
        }

        // 🔥 VEHICLE TYPE VALIDATION
        if (!vehicle.getVehicleType().equalsIgnoreCase(slot.getVehicleType())) {
            throw new CustomException("Vehicle type mismatch with slot");
        }

        // 🔐 Prevent double booking
        if (bookingRepository.existsBySlotIdAndStatus(slotId, "ACTIVE")) {
            throw new CustomException("Slot already booked");
        }

        // 🔒 LOCK SLOT
        slot.setStatus("OCCUPIED");
        slotRepository.save(slot);

        Booking booking = Booking.builder()
                .userId(userId)
                .vehicleId(vehicleId)
                .slotId(slotId)
                .buildingId(slot.getBuildingId())
                .startTime(LocalDateTime.now())
                .status("ACTIVE")
                .createdBy(userId)
                .createdOn(LocalDateTime.now())
                .build();

        return bookingRepository.save(booking);
    }
    /**
     * Get my bookings
     */
    @Override
    public List<Booking> getMyBookings() {

        Long userId = getCurrentUserId();

        return bookingRepository.findByUserId(userId);
    }

    /**
     * Complete booking (EXIT)
     */
    @Override
    public Booking completeBooking(Long bookingId) {

        Long userId = getCurrentUserId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException("Booking not found"));

        // 🔐 Ownership check
        if (!booking.getUserId().equals(userId)) {
            throw new CustomException("Access denied");
        }

        if (!"ACTIVE".equalsIgnoreCase(booking.getStatus())) {
            throw new CustomException("Booking already completed");
        }

        // ⏱️ Set end time
        booking.setEndTime(LocalDateTime.now());
        booking.setStatus("COMPLETED");
        booking.setUpdatedBy(userId);
        booking.setUpdatedOn(LocalDateTime.now());

        // 🔓 RELEASE SLOT
        Slot slot = slotRepository.findById(booking.getSlotId())
                .orElseThrow(() -> new CustomException("Slot not found"));

        slot.setStatus("AVAILABLE");
        slot.setUpdatedBy(userId);
        slot.setUpdatedOn(LocalDateTime.now());

        slotRepository.save(slot);

        return bookingRepository.save(booking);
    }

    /**
     * Cancel booking
     */
    @Override
    public Booking cancelBooking(Long bookingId) {

        Long userId = getCurrentUserId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException("Booking not found"));

        if (!booking.getUserId().equals(userId)) {
            throw new CustomException("Access denied");
        }

        if (!"ACTIVE".equalsIgnoreCase(booking.getStatus())) {
            throw new CustomException("Cannot cancel this booking");
        }

        booking.setStatus("CANCELLED");
        booking.setEndTime(LocalDateTime.now());
        booking.setUpdatedBy(userId);
        booking.setUpdatedOn(LocalDateTime.now());
        

        // 🔓 RELEASE SLOT
        Slot slot = slotRepository.findById(booking.getSlotId())
                .orElseThrow(() -> new CustomException("Slot not found"));

        slot.setStatus("AVAILABLE");
        slot.setUpdatedBy(userId);
        slot.setUpdatedOn(LocalDateTime.now());

        slotRepository.save(slot);

        return bookingRepository.save(booking);
    }
    
    @Override
    public List<Booking> getBookingsByUserId(Long userId) {

        Long currentUserId = getCurrentUserId();
        String role = jwtUtil.getCurrentUserRole();

        // 🔐 SECURITY CHECK
        if (!currentUserId.equals(userId) && !"ADMIN".equalsIgnoreCase(role)) {
            throw new CustomException("Access denied");
        }

        return bookingRepository.findByUserId(userId);
    }
    
    @Override
    public List<Booking> getAllBookings() {

        String role = jwtUtil.getCurrentUserRole();

        // 🔐 ADMIN ONLY
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new CustomException("Access denied: Admin only");
        }

        return bookingRepository.findAll();
    }
    
    @Override
    public List<Booking> getBookingsByStatus(String status) {

        if (!"ADMIN".equalsIgnoreCase(jwtUtil.getCurrentUserRole())) {
            throw new CustomException("Access denied");
        }

        return bookingRepository.findByStatus(status);
    }
   
    
}