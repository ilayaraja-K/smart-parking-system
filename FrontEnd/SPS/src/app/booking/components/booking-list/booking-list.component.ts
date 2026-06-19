import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Booking } from '../../services/booking.service';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-booking-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './booking-list.component.html'
})
export class BookingListComponent implements OnInit {
  bookings:  Booking[] = [];
  buildings: any[]     = [];
  activeTab: 'ACTIVE' | 'COMPLETED' = 'ACTIVE';
  loading = false;
  errorMsg = '';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const userId = this.authService.currentUser?.id ?? 0;
    if (!userId) {
      this.errorMsg = 'Session expired. Please log in again.';
      this.cdr.detectChanges();
      return;
    }
    this.loading = true;
    this.cdr.detectChanges();

    // Load buildings for name lookup
    this.http.get<any>('http://localhost:8091/pabsm/buildings').subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.buildings = Array.isArray(data) ? data : [];
        this.cdr.detectChanges();
      }
    });

    this.http.get<any>(`http://localhost:8091/pabsm/users/${userId}/bookings`).subscribe({
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

  get activeBookings():    Booking[] { return this.bookings.filter(b => b.status === 'ACTIVE'); }
  get completedBookings(): Booking[] { return this.bookings.filter(b => b.status === 'COMPLETED' || b.status === 'CANCELLED'); }

  safeDate(dt?: string): Date | null {
    if (!dt) return null;
    try {
      const clean = dt.replace(/(\.\d{3})\d+/, '$1');
      const d = new Date(clean);
      return isNaN(d.getTime()) ? null : d;
    } catch { return null; }
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

  complete(id: number): void {
    this.http.put<any>(`http://localhost:8091/pabsm/bookings/${id}/complete`, {}).subscribe({
      next: (res) => {
        const updated = res?.appResponse ?? res;
        const i = this.bookings.findIndex(b => b.id === id);
        if (i !== -1) this.bookings[i] = updated;
        this.cdr.detectChanges();
      },
      error: () => { this.errorMsg = 'Failed to complete booking.'; this.cdr.detectChanges(); }
    });
  }

  cancel(id: number): void {
    if (!confirm('Cancel this booking?')) return;
    this.http.delete<any>(`http://localhost:8091/pabsm/bookings/${id}`).subscribe({
      next: () => { this.bookings = this.bookings.filter(b => b.id !== id); this.cdr.detectChanges(); },
      error: () => { this.errorMsg = 'Failed to cancel booking.'; this.cdr.detectChanges(); }
    });
  }
}
