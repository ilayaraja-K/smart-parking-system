package com.myapp.vehicle.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myapp.auth.entity.User;
import com.myapp.auth.repository.UserRepository;
import com.myapp.exception.CustomException;
import com.myapp.security.jwt.JwtUtil;
import com.myapp.vehicle.entity.UserVehicle;
import com.myapp.vehicle.entity.Vehicle;
import com.myapp.vehicle.repository.UserVehicleRepository;
import com.myapp.vehicle.repository.VehicleRepository;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserVehicleRepository userVehicleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private User user;
    private Vehicle vehicle;

    @BeforeEach
    void setup() {

        user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .role("USER")
                .build();

        vehicle = Vehicle.builder()
                .id(1L)
                .vehicleNumber("TN01AB1234")
                .vehicleType("CAR")
                .createdOn(LocalDateTime.now())
                .createdBy(1L)
                .build();
    }

    // ================= REGISTER VEHICLE =================

    @Test
    void testRegisterVehicle_NewVehicle() {

        when(jwtUtil.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(vehicleRepository.findByVehicleNumber(any()))
        .thenReturn(Optional.empty());

        when(vehicleRepository.save(any())).thenReturn(vehicle);

        when(userVehicleRepository.existsByUserIdAndVehicleId(1L, 1L)).thenReturn(false);

        Vehicle result = vehicleService.registerVehicle(vehicle);

        assertNotNull(result);
        assertEquals("TN01AB1234", result.getVehicleNumber());
    }

    @Test
    void testRegisterVehicle_AlreadyMapped() {

        when(jwtUtil.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(vehicleRepository.findByVehicleNumber(any()))
        .thenReturn(Optional.of(vehicle));

        when(userVehicleRepository.existsByUserIdAndVehicleId(1L, 1L)).thenReturn(true);

        assertThrows(CustomException.class, () -> {
            vehicleService.registerVehicle(vehicle);
        });
    }

    // ================= GET VEHICLE =================

    @Test
    void testGetVehicleById_UserHasAccess() {

        when(jwtUtil.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        when(userVehicleRepository.existsByUserIdAndVehicleId(1L, 1L)).thenReturn(true);

        Vehicle result = vehicleService.getVehicleById(1L);

        assertNotNull(result);
    }

    @Test
    void testGetVehicleById_AccessDenied() {

        when(jwtUtil.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        when(userVehicleRepository.existsByUserIdAndVehicleId(1L, 1L)).thenReturn(false);

        assertThrows(CustomException.class, () -> {
            vehicleService.getVehicleById(1L);
        });
    }

    // ================= UPDATE VEHICLE =================

    @Test
    void testUpdateVehicle_OwnerSuccess() {

        when(jwtUtil.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        UserVehicle mapping = UserVehicle.builder()
                .userId(1L)
                .vehicleId(1L)
                .ownershipType("OWNER")
                .build();

        when(userVehicleRepository.findByVehicleId(1L)).thenReturn(List.of(mapping));

        when(vehicleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Vehicle updated = new Vehicle();
        updated.setModel("Updated Model");

        Vehicle result = vehicleService.updateVehicle(1L, updated);

        assertEquals("Updated Model", result.getModel());
    }

    @Test
    void testUpdateVehicle_NotOwner() {

        when(jwtUtil.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        UserVehicle mapping = UserVehicle.builder()
                .userId(1L)
                .vehicleId(1L)
                .ownershipType("FAMILY")
                .build();

        when(userVehicleRepository.findByVehicleId(1L)).thenReturn(List.of(mapping));

        Vehicle updated = new Vehicle();
        updated.setModel("Updated");

        assertThrows(CustomException.class, () -> {
            vehicleService.updateVehicle(1L, updated);
        });
    }
}