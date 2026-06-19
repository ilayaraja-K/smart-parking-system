import { Component, OnInit, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Vehicle } from '../../services/vehicle.service';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-vehicle-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.Default,
  imports: [CommonModule, RouterLink],
  templateUrl: './vehicle-list.component.html'
})
export class VehicleListComponent implements OnInit {
  vehicles: Vehicle[] = [];
  activeTab: 'CAR' | 'BIKE' = 'CAR';
  loading = false;
  errorMsg = '';
  successMsg = '';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void { this.loadVehicles(); }

  loadVehicles(): void {
    const userId = this.authService.currentUser?.id;
    if (!userId) {
      this.errorMsg = 'Session expired. Please log out and log in again.';
      this.cdr.detectChanges();
      return;
    }

    this.loading  = true;
    this.errorMsg = '';
    this.cdr.detectChanges();

    this.http.get<any>(`http://localhost:8090/uvmgmt/vehicles/user/${userId}`).subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.vehicles = Array.isArray(data) ? data : [];
        this.loading  = false;
        this.cdr.detectChanges();   // force re-render
      },
      error: (err) => {
        this.errorMsg = `Error ${err?.status ?? 'unknown'}: Failed to load vehicles.`;
        this.loading  = false;
        this.cdr.detectChanges();
      }
    });
  }

  forceLogout(): void { this.authService.logout(); }

  get filteredVehicles(): Vehicle[] { return this.vehicles.filter(v => v.vehicleType === this.activeTab); }
  get carCount():  number { return this.vehicles.filter(v => v.vehicleType === 'CAR').length; }
  get bikeCount(): number { return this.vehicles.filter(v => v.vehicleType === 'BIKE').length; }
  setTab(tab: 'CAR' | 'BIKE'): void { this.activeTab = tab; this.cdr.detectChanges(); }

  delete(id: number): void {
    if (!confirm('Delete this vehicle?')) return;
    this.http.delete(`http://localhost:8090/uvmgmt/vehicles/${id}`).subscribe({
      next: () => {
        this.vehicles   = this.vehicles.filter(v => v.id !== id);
        this.successMsg = 'Vehicle deleted.';
        this.cdr.detectChanges();
        setTimeout(() => { this.successMsg = ''; this.cdr.detectChanges(); }, 3000);
      },
      error: () => { this.errorMsg = 'Failed to delete vehicle.'; this.cdr.detectChanges(); }
    });
  }
}



