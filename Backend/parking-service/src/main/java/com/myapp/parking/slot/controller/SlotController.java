package com.myapp.parking.slot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myapp.parking.common.AppResponse;
import com.myapp.parking.common.MyServiceMessage;
import com.myapp.parking.slot.entity.Slot;
import com.myapp.parking.slot.service.SlotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Slot Management", description = "APIs for slot operations")
@RestController
@RequestMapping("/pabsm/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    /**
     * Create Slot (ADMIN only)
     */
    @Operation(summary = "Create slots for a building")
    @PostMapping
    public ResponseEntity<AppResponse<Slot>> createSlot(@RequestBody Slot slot) {

        Slot saved = slotService.createSlot(slot);

        return ResponseEntity.ok(
                AppResponse.createSuccessfullyCreatedMessage(
                        MyServiceMessage.CREATED,
                        saved
                )
        );
    }

    /**
     * Get all slots
     */
    @Operation(summary = "Get the slots in the backend(admin purpose)")
    @GetMapping
    public ResponseEntity<AppResponse<List<Slot>>> getAllSlots() {

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        slotService.getAllSlots()
                )
        );
    }

    /**
     * Get slots by building
     */
    @Operation(summary = "Get slots of building")
    @GetMapping("/building/{buildingId}")
    public ResponseEntity<AppResponse<List<Slot>>> getSlotsByBuilding(
            @PathVariable Long buildingId) {

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        slotService.getSlotsByBuilding(buildingId)
                )
        );
    }

    /**
     * Get available slots (FILTER)
     */
    @Operation(summary = "Get the available slots in a building")
    @GetMapping("/building/{buildingId}/available")
    public ResponseEntity<AppResponse<List<Slot>>> getAvailableSlots(
            @PathVariable Long buildingId,
            @RequestParam String vehicleType) {

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        slotService.getAvailableSlots(buildingId, vehicleType)
                )
        );
    }
    
    @Operation(summary = "ADMIN: Delete a slot (blocked if active booking)")
    @DeleteMapping("/{slotId}")
    public ResponseEntity<AppResponse<String>> deleteSlot(@PathVariable Long slotId) {

        slotService.deleteSlot(slotId);

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        "Slot deleted successfully"
                )
        );
    }
}