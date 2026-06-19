package com.myapp.parking.booking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a parking booking
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    /**
     * User ID (from Auth Service)
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * Vehicle ID (from Vehicle Service)
     */
    @Column(nullable = false)
    private Long vehicleId;

    /**
     * Slot ID
     */
    @Column(nullable = false)
    private Long slotId;

    /**
     * Building ID
     */
    @Column(nullable = false)
    private Long buildingId;

    /**
     * Start time of parking
     */
    @Column(nullable = false)
    private LocalDateTime startTime;

    /**
     * End time of parking (set on exit)
     */
    private LocalDateTime endTime;

    /**
     * Booking status
     */
    @Column(nullable = false)
    private String status; // ACTIVE / COMPLETED / CANCELLED

    // 🔐 Audit Fields (MANDATORY)

    @Column(nullable = false, updatable = false)
    private Long createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdOn;

    private Long updatedBy;

    private LocalDateTime updatedOn;
}