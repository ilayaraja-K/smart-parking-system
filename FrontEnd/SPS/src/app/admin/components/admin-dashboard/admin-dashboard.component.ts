import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Building } from '../../../building/services/building.service';
import { Booking } from '../../../booking/services/booking.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-dashboard.component.html'
})
export class AdminDashboardComponent implements OnInit {
  buildings:   Building[] = [];
  allBookings: Booking[]  = [];
  users:       any[]      = [];

  activeCount:    number = 0;
  completedCount: number = 0;
  cancelledCount: number = 0;
  totalUsers:     number = 0;

  recentBookings: Booking[] = [];
  loading = false;
  errorMsg = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    // Load buildings
    this.http.get<any>('https://parking-service-un8u.onrender.com/pabsm/buildings').subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.buildings = Array.isArray(data) ? data : [];
        this.cdr.detectChanges();
      },
      error: () => { this.cdr.detectChanges(); }
    });

    // Load all bookings
    this.http.get<any>('https://parking-service-un8u.onrender.com/pabsm/bookings').subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.allBookings    = Array.isArray(data) ? data : [];
        this.activeCount    = this.allBookings.filter(b => b.status === 'ACTIVE').length;
        this.completedCount = this.allBookings.filter(b => b.status === 'COMPLETED').length;
        this.cancelledCount = this.allBookings.filter(b => b.status === 'CANCELLED').length;
        this.recentBookings = [...this.allBookings]
          .sort((a, b) => (b.startTime ?? '').localeCompare(a.startTime ?? ''))
          .slice(0, 5);
        this.cdr.detectChanges();
      }
    });

    // Load users
    this.http.get<any>('https://myapp-service-s92w.onrender.com/uvmgmt/users').subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.users      = Array.isArray(data) ? data : [];
        this.totalUsers = this.users.length;
        this.cdr.detectChanges();
      }
    });
  }

  get totalBuildings(): number { return this.buildings.length; }
  get totalBookings():  number { return this.allBookings.length; }

  getBuildingName(buildingId: number | undefined): string {
    if (!buildingId) return '—';
    const b = this.buildings.find(x => (x.buildingId ?? x.id) === buildingId);
    return b ? (b.name ?? b.buildingName ?? `#${buildingId}`) : `#${buildingId}`;
  }

  getUserName(userId: number | undefined): string | undefined {
    if (!userId) return undefined;
    const u = this.users.find(x => (x.id ?? x.userId) === userId);
    return u?.name ?? undefined;
  }

  safeDate(dt?: string): Date | null {
    if (!dt) return null;
    try {
      const clean = dt.replace(/(\.\d{3})\d+/, '$1');
      const d = new Date(clean);
      return isNaN(d.getTime()) ? null : d;
    } catch { return null; }
  }

  statusClass(status: string): string {
    const map: Record<string, string> = { ACTIVE: 'success', COMPLETED: 'secondary', CANCELLED: 'danger' };
    return `badge bg-${map[status] ?? 'secondary'}`;
  }
}
