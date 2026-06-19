import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Booking, BookingService } from '../../services/booking.service';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-booking-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './booking-detail.component.html'
})
export class BookingDetailComponent implements OnInit {
  booking: Booking | null = null;
  loading = false;
  errorMsg = '';

  constructor(
    private bookingService: BookingService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const bookingId = Number(this.route.snapshot.paramMap.get('id'));
    const user = this.authService.currentUser;
    if (!user) { this.errorMsg = 'Not authenticated.'; return; }
    this.loading = true;
    // Load all user bookings then find the one we need
    this.bookingService.getMyBookings(user.id).subscribe({
      next: (list: Booking[]) => {
        this.booking = list.find((b: Booking) => b.id === bookingId) ?? null;
        if (!this.booking) this.errorMsg = 'Booking not found.';
        this.loading = false;
      },
      error: () => { this.errorMsg = 'Failed to load booking details.'; this.loading = false; }
    });
  }

  statusClass(status: string): string {
    const map: Record<string, string> = { ACTIVE: 'success', COMPLETED: 'secondary', CANCELLED: 'danger' };
    return `badge bg-${map[status] ?? 'secondary'} fs-6`;
  }
}
