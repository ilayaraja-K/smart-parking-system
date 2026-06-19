package com.myapp.vehicle.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myapp.common.AppResponse;
import com.myapp.common.MyServiceMessage;
import com.myapp.vehicle.dto.VehicleUserResponse;
import com.myapp.vehicle.entity.Vehicle;
import com.myapp.vehicle.service.VehicleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/uvmgmt/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * Register vehicle
     */
    @PostMapping("/register")
    public ResponseEntity<AppResponse<Vehicle>> registerVehicle(@RequestBody Vehicle vehicle) {

        Vehicle savedVehicle = vehicleService.registerVehicle(vehicle);

        return ResponseEntity.ok(
                AppResponse.createSuccessfullyCreatedMessage(
                        MyServiceMessage.CREATED,
                        savedVehicle
                )
        );
    }

    /**
     * Get vehicle by ID
     */
    @GetMapping("/{vehicleId}")
    public ResponseEntity<AppResponse<Vehicle>> getVehicleById(@PathVariable Long vehicleId) {

        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        vehicle
                )
        );
    }

    /**
     * 🔥 Get vehicles by userId (SELF + ADMIN)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<AppResponse<List<Vehicle>>> getVehiclesByUserId(
            @PathVariable Long userId) {

        List<Vehicle> vehicles = vehicleService.getVehiclesByUserIdSecure(userId);

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        vehicles
                )
        );
    }

    /**
     * Update vehicle
     */
    @PutMapping("/{vehicleId}")
    public ResponseEntity<AppResponse<Vehicle>> updateVehicle(
            @PathVariable Long vehicleId,
            @RequestBody Vehicle vehicle) {

        Vehicle updatedVehicle = vehicleService.updateVehicle(vehicleId, vehicle);

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        updatedVehicle
                )
        );
    }

    /**
     * Remove vehicle mapping
     */
    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<AppResponse<Object>> removeVehicle(@PathVariable Long vehicleId) {

        vehicleService.removeVehicle(vehicleId);

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        "Vehicle removed successfully"
                )
        );
    }

    /**
     * 🔥 ADMIN: Get all vehicles
     */
    @GetMapping
    public ResponseEntity<AppResponse<List<VehicleUserResponse>>> getAllVehicles() {

        List<VehicleUserResponse> vehicles = vehicleService.getAllVehicles();

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        vehicles
                )
        );
    }

    /**
     * 🔥 ADMIN: Get users using a vehicle
     */
    @GetMapping("/{vehicleId}/users")
    public ResponseEntity<AppResponse<List<Long>>> getUsersByVehicleId(
            @PathVariable Long vehicleId) {

        List<Long> users = vehicleService.getUsersByVehicleId(vehicleId);

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        users
                )
        );
    }
}