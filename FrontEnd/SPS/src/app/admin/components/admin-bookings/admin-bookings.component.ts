import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Booking } from '../../../booking/services/booking.service';
import { Building } from '../../../building/services/building.service';

type StatusTab = 'ALL' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';

@Component({
  selector: 'app-admin-bookings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-bookings.component.html'
})
export class AdminBookingsComponent implements OnInit {
  bookings:  Booking[]  = [];
  buildings: Building[] = [];
  users:     any[]      = [];
  activeTab: StatusTab = 'ALL';
  tabs: StatusTab[] = ['ALL', 'ACTIVE', 'COMPLETED', 'CANCELLED'];
  loading = false;
  errorMsg = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    // Load buildings for name lookup
    this.http.get<any>('https://parking-service-un8u.onrender.com/pabsm/buildings').subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.buildings = Array.isArray(data) ? data : [];
        this.cdr.detectChanges();
      }
    });
    // Load users for name lookup
    this.http.get<any>('https://myapp-service-s92w.onrender.com/uvmgmt/users').subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.users = Array.isArray(data) ? data : [];
        this.cdr.detectChanges();
      }
    });
    this.loadTab('ALL');
  }

  loadTab(tab: StatusTab): void {
    this.activeTab = tab;
    this.loading   = true;
    this.errorMsg  = '';
    this.bookings  = [];
    this.cdr.detectChanges();

    const url = tab === 'ALL'
      ? 'https://parking-service-un8u.onrender.com/pabsm/bookings'
      : `https://parking-service-un8u.onrender.com/pabsm/bookings/status/${tab}`;

    this.http.get<any>(url).subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.bookings = Array.isArray(data) ? data : [];
        this.loading  = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMsg = `Error ${err?.status ?? 'unknown'}: Failed to load bookings.`;
        this.loading  = false;
        this.cdr.detectChanges();
      }
    });
  }

  getBuildingName(buildingId: number | undefined): string {
    if (!buildingId) return '—';
    const b = this.buildings.find(x => (x.buildingId ?? x.id) === buildingId);
    return b ? (b.name ?? b.buildingName ?? `#${buildingId}`) : `#${buildingId}`;
  }

  getBuildingCity(buildingId: number | undefined): string {
    if (!buildingId) return '';
    const b = this.buildings.find(x => (x.buildingId ?? x.id) === buildingId);
    return b?.city ?? '';
  }

  getUserName(userId: number | undefined): string | undefined {
    if (!userId) return undefined;
    const u = this.users.find(x => (x.id ?? x.userId) === userId);
    return u?.name ?? undefined;
  }

  getDuration(startTime?: string, endTime?: string): string {
    if (!startTime || !endTime) return '—';
    const ms = new Date(endTime).getTime() - new Date(startTime).getTime();
    if (ms <= 0) return '—';
    const totalMins = Math.floor(ms / 60000);
    const hrs  = Math.floor(totalMins / 60);
    const mins = totalMins % 60;
    return hrs > 0 ? `${hrs}h ${mins}m` : `${mins}m`;
  }

  /** Truncate Java's nanosecond LocalDateTime to milliseconds for Angular date pipe */
  safeDate(dt?: string): Date | null {
    if (!dt) return null;
    try {
      // "2026-04-01T10:29:26.750821" → truncate to 3 decimal places
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
