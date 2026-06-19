package com.myapp.parking.booking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myapp.parking.booking.dto.BookingRequest;
import com.myapp.parking.booking.entity.Booking;
import com.myapp.parking.booking.service.BookingService;
import com.myapp.parking.common.AppResponse;
import com.myapp.parking.common.MyServiceMessage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Booking Management", description = "APIs for booking operations")
@RestController
@RequestMapping("/pabsm")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Create booking
     */
    @Operation(summary = "Make booking")
    @PostMapping("/bookings")
    public ResponseEntity<AppResponse<Booking>> createBooking(
            @RequestBody BookingRequest request) {

        Booking booking = bookingService.createBooking(
                request.getVehicleId(),
                request.getSlotId()
        );

        return ResponseEntity.ok(
                AppResponse.createSuccessfullyCreatedMessage(
                        MyServiceMessage.CREATED,
                        booking
                )
        );
    }

    /**
     * 🔥 ENTERPRISE VERSION
     * Get bookings by userId (SECURE)
     */
    @Operation(summary = "Get the Booking made by the user")
    @GetMapping("/users/{userId}/bookings")
    public ResponseEntity<AppResponse<List<Booking>>> getUserBookings(
            @PathVariable Long userId) {

        List<Booking> bookings = bookingService.getBookingsByUserId(userId);

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        bookings
                )
        );
    }

    /**
     * Complete booking
     */
    @Operation(summary = "Leaving the Building")
    @PutMapping("/bookings/{bookingId}/complete")
    public ResponseEntity<AppResponse<Booking>> completeBooking(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        bookingService.completeBooking(bookingId)
                )
        );
    }

    /**
     * Cancel booking
     */
    @Operation(summary = "Cancelling the Booking")
    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<AppResponse<Booking>> cancelBooking(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        bookingService.cancelBooking(bookingId)
                )
        );
    }
    
    @Operation(summary = "ADMIN: Get all bookings")
    @GetMapping("/bookings")
    public ResponseEntity<AppResponse<List<Booking>>> getAllBookings() {

        List<Booking> bookings = bookingService.getAllBookings();

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        bookings
                )
        );
    }
    @Operation(summary="ADMIN: Get the Bookings by status ")
    @GetMapping("/bookings/status/{status}")
    public ResponseEntity<AppResponse<List<Booking>>> getBookingsByStatus(
            @PathVariable String status) {

        List<Booking> bookings = bookingService.getBookingsByStatus(status);

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        bookings
                )
        );
    }
}