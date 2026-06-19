package com.myapp.parking.building.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myapp.parking.building.entity.Building;
import com.myapp.parking.building.service.BuildingService;
import com.myapp.parking.common.AppResponse;
import com.myapp.parking.common.MyServiceMessage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Building Management", description = "APIs for building operations")
@RestController
@RequestMapping("/pabsm/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    /**
     * Create Building (ADMIN only)
     */
    @Operation(summary = "Create building by admin")
    @PostMapping
    public ResponseEntity<AppResponse<Building>> createBuilding(@RequestBody Building building) {

        Building saved = buildingService.createBuilding(building);

        return ResponseEntity.ok(
                AppResponse.createSuccessfullyCreatedMessage(
                        MyServiceMessage.CREATED,
                        saved
                )
        );
    }

    /**
     * Get all buildings OR filter by city
     */
    @Operation(summary = "Get the Buildings which has parkings")
    @GetMapping
    public ResponseEntity<AppResponse<List<Building>>> getBuildings(
            @RequestParam(required = false) String city) {

        List<Building> buildings = (city != null && !city.isEmpty())
                ? buildingService.getBuildingsByCity(city)
                : buildingService.getAllBuildings();

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        buildings
                )
        );
    }
}