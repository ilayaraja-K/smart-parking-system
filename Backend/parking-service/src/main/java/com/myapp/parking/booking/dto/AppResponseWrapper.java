package com.myapp.parking.booking.dto;

import com.myapp.parking.common.MyServiceMessage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppResponseWrapper<T> {

    private MyServiceMessage message;   // 🔥 CHANGE THIS

    private T appResponse;
}