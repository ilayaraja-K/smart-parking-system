package com.myapp.parking.slot.service;

import java.util.List;

import com.myapp.parking.slot.entity.Slot;

/**
 * Service interface for Slot operations
 */
public interface SlotService {

    /**
     * Create a new slot (ADMIN only)
     * - Sets status = AVAILABLE
     * - Sets audit fields
     */
    Slot createSlot(Slot slot);

    /**
     * Get all slots
     */
    List<Slot> getAllSlots();

    /**
     * Get slots by building
     */
    List<Slot> getSlotsByBuilding(Long buildingId);

    /**
     * Get available slots by vehicle type
     */
    List<Slot> getAvailableSlots(Long buildingId, String vehicleType);
    
    /**
     * Delete a slot (ADMIN only)
     * - Blocked if slot has an ACTIVE booking
     */
    void deleteSlot(Long slotId);
    
    
}