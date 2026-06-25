import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Building } from '../../../building/services/building.service';
import { ParkingSlot } from '../../../booking/services/booking.service';

interface BuildingSlots {
  building: Building;
  slots: ParkingSlot[];
  loading: boolean;
  expanded: boolean;
}

@Component({
  selector: 'app-admin-slots',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-slots.component.html'
})
export class AdminSlotsComponent implements OnInit {
  // Overview — all buildings with their slots
  buildingSlots: BuildingSlots[] = [];
  overviewLoading = false;

  // Create form
  slotForm: FormGroup;
  buildings: Building[] = [];
  selectedBuildingId: number | null = null;
  formLoading = false;
  successMsg = '';
  errorMsg = '';

  showCreateForm = false;

  constructor(private http: HttpClient, private fb: FormBuilder, private cdr: ChangeDetectorRef) {
    this.slotForm = this.fb.group({
      buildingId:  ['', Validators.required],
      slotNumber:  ['', Validators.required],
      vehicleType: ['CAR', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadOverview();
  }

  loadOverview(): void {
    this.overviewLoading = true;
    this.cdr.detectChanges();
    this.http.get<any>('https://parking-service-un8u.onrender.com/pabsm/buildings').subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.buildings = Array.isArray(data) ? data : [];
        // Build skeleton rows
        this.buildingSlots = this.buildings.map(b => ({
          building: b, slots: [], loading: true, expanded: true
        }));
        this.overviewLoading = false;
        this.cdr.detectChanges();
        // Load slots for each building
        this.buildingSlots.forEach((row, i) => {
          const id = row.building.buildingId ?? row.building.id;
          this.http.get<any>(`https://parking-service-un8u.onrender.com/pabsm/slots/building/${id}`).subscribe({
            next: (r) => {
              const d = r?.appResponse ?? r;
              this.buildingSlots[i].slots   = Array.isArray(d) ? d : [];
              this.buildingSlots[i].loading = false;
              this.cdr.detectChanges();
            },
            error: () => { this.buildingSlots[i].loading = false; this.cdr.detectChanges(); }
          });
        });
      },
      error: () => { this.overviewLoading = false; this.cdr.detectChanges(); }
    });
  }

  toggle(row: BuildingSlots): void {
    row.expanded = !row.expanded;
    this.cdr.detectChanges();
  }

  deleteSlot(row: BuildingSlots, slot: ParkingSlot, event: Event): void {
    event.stopPropagation();
    if (slot.status === 'OCCUPIED') {
      this.errorMsg = `Cannot delete slot "${slot.slotNumber}" — it has an active booking.`;
      this.cdr.detectChanges();
      setTimeout(() => { this.errorMsg = ''; this.cdr.detectChanges(); }, 4000);
      return;
    }
    if (!confirm(`Delete slot "${slot.slotNumber}"? This cannot be undone.`)) return;
    const slotId = slot.slotId ?? slot.id;
    this.http.delete<any>(`https://parking-service-un8u.onrender.com/pabsm/slots/${slotId}`).subscribe({
      next: () => {
        row.slots = row.slots.filter(s => (s.slotId ?? s.id) !== slotId);
        this.successMsg = `Slot "${slot.slotNumber}" deleted.`;
        this.cdr.detectChanges();
        setTimeout(() => { this.successMsg = ''; this.cdr.detectChanges(); }, 3000);
      },
      error: (err) => {
        const msg = err?.error?.message ?? err?.error?.appResponse ?? 'Failed to delete slot.';
        this.errorMsg = typeof msg === 'string' ? msg : JSON.stringify(msg);
        this.cdr.detectChanges();
        setTimeout(() => { this.errorMsg = ''; this.cdr.detectChanges(); }, 4000);
      }
    });
  }

  availableCount(row: BuildingSlots): number {
    return row.slots.filter(s => s.status === 'AVAILABLE').length;
  }

  occupiedCount(row: BuildingSlots): number {
    return row.slots.filter(s => s.status === 'OCCUPIED').length;
  }

  onBuildingSelect(event: Event): void {
    this.selectedBuildingId = Number((event.target as HTMLSelectElement).value) || null;
  }

  onSubmit(): void {
    if (this.slotForm.invalid) { this.slotForm.markAllAsTouched(); return; }
    this.formLoading = true;
    this.errorMsg = '';
    this.cdr.detectChanges();
    this.http.post<any>('https://parking-service-un8u.onrender.com/pabsm/slots', this.slotForm.value).subscribe({
      next: (res) => {
        const slot = res?.appResponse ?? res;
        this.formLoading = false;
        this.successMsg  = `Slot "${slot.slotNumber}" created!`;
        this.slotForm.patchValue({ slotNumber: '' });
        this.cdr.detectChanges();
        setTimeout(() => { this.successMsg = ''; this.cdr.detectChanges(); }, 3000);
        // Refresh overview
        this.loadOverview();
      },
      error: (err) => {
        this.formLoading = false;
        this.errorMsg    = err?.error?.message || 'Failed to create slot.';
        this.cdr.detectChanges();
      }
    });
  }
}


