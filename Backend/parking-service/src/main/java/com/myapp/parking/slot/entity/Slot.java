package com.myapp.parking.slot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "slots",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"building_id", "slot_number"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id") // ✅ CHANGED
    private Long slotId;

    @Column(name = "slot_number", nullable = false)
    private String slotNumber;

    @Column(name = "building_id", nullable = false)
    private Long buildingId;

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    private String floor;
    private String section;

    @Column(nullable = false)
    private String status; // AVAILABLE / OCCUPIED

    @Column(nullable = false, updatable = false)
    private Long createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdOn;

    private Long updatedBy;
    private LocalDateTime updatedOn;
}