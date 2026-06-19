package com.myapp.parking.booking.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.myapp.parking.booking.dto.AppResponseWrapper;
import com.myapp.parking.booking.dto.VehicleResponse;
import com.myapp.parking.booking.entity.Booking;
import com.myapp.parking.booking.repository.BookingRepository;
import com.myapp.parking.exception.CustomException;
import com.myapp.parking.security.jwt.JwtUtil;
import com.myapp.parking.slot.entity.Slot;
import com.myapp.parking.slot.repository.SlotRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Slot slot;

    @BeforeEach
    void setUp() {
        slot = Slot.builder()
                .slotId(1L)
                .buildingId(100L)
                .vehicleType("CAR")
                .status("AVAILABLE")
                .createdOn(LocalDateTime.now())
                .createdBy(1L)
                .build();
    }

    @Test
    void testCreateBooking_Success() {

        // Mock JWT
        when(jwtUtil.getCurrentUserId()).thenReturn(1L);

        // Mock Slot
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        // No active booking
        when(bookingRepository.findByUserIdAndStatus(1L, "ACTIVE"))
                .thenReturn(Optional.empty());

        when(bookingRepository.existsBySlotIdAndStatus(1L, "ACTIVE"))
                .thenReturn(false);

        // 🔥 MOCK VEHICLE RESPONSE (IMPORTANT)
        BookingServiceImpl spyService = Mockito.spy(bookingService);	

        VehicleResponse vehicle = new VehicleResponse();
        vehicle.setVehicleType("CAR");

        doReturn(vehicle).when(spyService).validateVehicle(1L);

        // Mock save
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = spyService.createBooking(1L, 1L);

        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void testCreateBooking_SlotNotAvailable() {

        when(jwtUtil.getCurrentUserId()).thenReturn(1L);

        slot.setStatus("OCCUPIED");

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        BookingServiceImpl spyService = Mockito.spy(bookingService);

        VehicleResponse vehicle = new VehicleResponse();
        vehicle.setVehicleType("CAR");

        doReturn(vehicle).when(spyService).validateVehicle(1L);

        assertThrows(CustomException.class, () -> {
            spyService.createBooking(1L, 1L);
        });
    }

    @Test
    void testCompleteBooking_Success() {

        when(jwtUtil.getCurrentUserId()).thenReturn(1L);

        Booking booking = Booking.builder()
                .id(1L)
                .userId(1L)
                .slotId(1L)
                .status("ACTIVE")
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Booking result = bookingService.completeBooking(1L);

        assertEquals("COMPLETED", result.getStatus());
    }

    @Test
    void testCancelBooking_Success() {

        when(jwtUtil.getCurrentUserId()).thenReturn(1L);

        Booking booking = Booking.builder()
                .id(1L)
                .userId(1L)
                .slotId(1L)
                .status("ACTIVE")
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Booking result = bookingService.cancelBooking(1L);

        assertEquals("CANCELLED", result.getStatus());
    }
}