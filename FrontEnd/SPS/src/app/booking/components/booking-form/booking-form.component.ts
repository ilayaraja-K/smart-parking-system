import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Building } from '../../../building/services/building.service';
import { Vehicle } from '../../../vehicle/services/vehicle.service';
import { ParkingSlot } from '../../services/booking.service';
import { AuthService } from '../../../auth/services/auth.service';

type Step = 1 | 2 | 3 | 4;

@Component({
  selector: 'app-booking-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './booking-form.component.html'
})
export class BookingFormComponent implements OnInit {
  step: Step = 1;

  // Step 1 — Building
  buildings: Building[] = [];
  selectedBuilding: Building | null = null;
  citySearch = new FormControl('');
  buildingsLoading = false;

  // Step 2 — Vehicle
  vehicles: Vehicle[] = [];
  selectedVehicle: Vehicle | null = null;

  // Step 3 — Slot
  slots: ParkingSlot[] = [];
  selectedSlot: ParkingSlot | null = null;
  slotsLoading = false;

  // Step 4 — Confirm
  booking = false;
  successMsg = '';
  errorMsg = '';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadBuildings();
    this.loadVehicles();
  }

  // ── Step 1 ──────────────────────────────────────────
  loadBuildings(): void {
    this.buildingsLoading = true;
    this.errorMsg = '';
    this.cdr.detectChanges();

    const city = this.citySearch.value?.trim();
    const url = city
      ? `https://parking-service-un8u.onrender.com/pabsm/buildings?city=${encodeURIComponent(city)}`
      : `https://parking-service-un8u.onrender.com/pabsm/buildings`;

    this.http.get<any>(url).subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.buildings = Array.isArray(data) ? data : [];
        this.buildingsLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMsg = `Error ${err?.status ?? 'unknown'}: Failed to load buildings.`;
        this.buildingsLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  selectBuilding(b: Building): void {
    this.selectedBuilding = b;
    this.step = 2;
    this.cdr.detectChanges();
  }

  // ── Step 2 ──────────────────────────────────────────
  loadVehicles(): void {
    const userId = this.authService.currentUser?.id ?? 0;
    if (!userId) return;
    this.http.get<any>(`https://myapp-service-s92w.onrender.com/uvmgmt/vehicles/user/${userId}`).subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.vehicles = Array.isArray(data) ? data : [];
        this.cdr.detectChanges();
      },
      error: () => { this.cdr.detectChanges(); }
    });
  }

  selectVehicle(v: Vehicle): void {
    this.selectedVehicle = v;
    this.step = 3;
    this.cdr.detectChanges();
    this.loadSlots();
  }

  // ── Step 3 ──────────────────────────────────────────
  loadSlots(): void {
    const buildingId = this.selectedBuilding?.buildingId ?? this.selectedBuilding?.id;
    const vehicleType = this.selectedVehicle?.vehicleType;
    if (!buildingId || !vehicleType) return;

    this.slotsLoading = true;
    this.slots = [];
    this.errorMsg = '';
    this.cdr.detectChanges();

    const url = `https://parking-service-un8u.onrender.com/pabsm/slots/building/${buildingId}/available?vehicleType=${vehicleType}`;
    this.http.get<any>(url).subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.slots = Array.isArray(data) ? data : [];
        this.slotsLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMsg = `Error ${err?.status ?? 'unknown'}: Failed to load slots.`;
        this.slotsLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  selectSlot(s: ParkingSlot): void {
    this.selectedSlot = s;
    this.step = 4;
    this.cdr.detectChanges();
  }

  // ── Step 4 — Confirm ────────────────────────────────
  confirmBooking(): void {
    this.booking  = true;
    this.errorMsg = '';
    this.cdr.detectChanges();

    const vehicleId = this.selectedVehicle?.id;
    const slotId    = this.selectedSlot?.slotId ?? this.selectedSlot?.id;

    this.http.post<any>(`https://parking-service-un8u.onrender.com/pabsm/bookings`, { vehicleId, slotId }).subscribe({
      next: () => {
        this.booking    = false;
        this.successMsg = 'Booking confirmed successfully!';
        this.cdr.detectChanges();
        setTimeout(() => this.router.navigate(['/bookings']), 1500);
      },
      error: (err) => {
        this.booking  = false;
        this.errorMsg = err?.error?.message?.responseMessage
          || err?.error?.message
          || 'Booking failed. Please try again.';
        this.cdr.detectChanges();
      }
    });
  }

  goBack(): void {
    if (this.step > 1) {
      this.step = (this.step - 1) as Step;
      this.cdr.detectChanges();
    }
  }
}
