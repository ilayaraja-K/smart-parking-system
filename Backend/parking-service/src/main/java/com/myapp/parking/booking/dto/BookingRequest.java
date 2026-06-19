package com.myapp.parking.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {
    private Long vehicleId;
    private Long slotId;
}