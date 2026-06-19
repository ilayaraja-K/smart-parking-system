package com.myapp.vehicle.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * Mapping table between User and Vehicle
 */
@Entity
@Table(
    name = "user_vehicles",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "vehicleId"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVehicle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_vehicle_id") // 🔥 ADD
	private Long id;

    // 🔗 USER ID (from Auth Service)
	@Column(name = "user_id", nullable = false)
	private Long userId;

    // 🔗 VEHICLE ID (from Vehicle master table)
	@Column(name = "vehicle_id", nullable = false)
	private Long vehicleId;

    // 🔥 Ownership Type
    @Column(nullable = false)
    private String ownershipType; // OWNER / FAMILY / TEMP

    // 🔐 Audit
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    
    @Column(nullable = false, updatable = false)
    private Long createdBy;

    private Long updatedBy;
}