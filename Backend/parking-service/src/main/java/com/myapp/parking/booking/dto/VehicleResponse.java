package com.myapp.parking.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleResponse {

    private Long id;
    private String vehicleNumber;
    private String vehicleType;
    private String model;

    private String createdOn;
    private String updatedOn;

    private Long createdBy;
    private Long updatedBy;
}