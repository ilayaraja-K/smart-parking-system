import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AppUser } from '../../services/admin.service';

interface VehicleRow {
  vehicleId: number;
  vehicleNumber: string;
  vehicleType: string;
  model: string;
  ownershipType?: string;
}

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-users.component.html'
})
export class AdminUsersComponent implements OnInit {
  users: AppUser[] = [];
  loading = false;
  errorMsg = '';

  // expanded user id → their vehicles
  expandedUserId: number | null = null;
  vehiclesMap: Record<number, VehicleRow[]> = {};
  vehiclesLoading = false;

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loading = true;
    this.cdr.detectChanges();
    this.http.get<any>('https://myapp-service-s92w.onrender.com/uvmgmt/users').subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.users   = Array.isArray(data) ? data : [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMsg = `Error ${err?.status ?? 'unknown'}: Failed to load users.`;
        this.loading  = false;
        this.cdr.detectChanges();
      }
    });
  }

  toggleVehicles(userId: number): void {
    if (this.expandedUserId === userId) {
      this.expandedUserId = null;
      this.cdr.detectChanges();
      return;
    }
    this.expandedUserId = userId;
    // Use cached result if already loaded
    if (this.vehiclesMap[userId]) {
      this.cdr.detectChanges();
      return;
    }
    this.vehiclesLoading = true;
    this.cdr.detectChanges();
    this.http.get<any>(`https://myapp-service-s92w.onrender.com/uvmgmt/vehicles/user/${userId}`).subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.vehiclesMap[userId] = Array.isArray(data) ? data : [];
        this.vehiclesLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.vehiclesMap[userId] = [];
        this.vehiclesLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  roleClass(role: string): string {
    return `badge ${role?.toUpperCase() === 'ADMIN' ? 'bg-danger' : 'bg-primary'}`;
  }
}
