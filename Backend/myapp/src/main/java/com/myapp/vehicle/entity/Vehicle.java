package com.myapp.vehicle.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * Master table for vehicles (unique vehicles globally)
 */
@Entity
@Table(
    name = "vehicles",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "vehicleNumber")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vehicle_id") // 🔥 ADD THIS
	private Long id;

    // 🔥 UNIQUE VEHICLE NUMBER (GLOBAL)
    @Column(nullable = false, unique = true)
    private String vehicleNumber;

    @Column(nullable = false)
    private String vehicleType; // CAR / BIKE

    private String model;

    // 🔐 Audit Fields (still important)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;
    
    @Column(nullable = false, updatable = false)
    private Long createdBy;

    private Long updatedBy;	
}