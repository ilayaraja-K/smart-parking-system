package com.myapp.vehicle.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleUserResponse {

    private Long vehicleId;
    private String vehicleNumber;
    private String vehicleType;
    private String model;

    private Long userId;
    private String ownershipType;
}