import { Routes } from '@angular/router';
import { BookingListComponent } from './components/booking-list/booking-list.component';
import { BookingFormComponent } from './components/booking-form/booking-form.component';
import { AuthGuard } from '../auth/guards/auth.guard';

export const bookingRoutes: Routes = [
  { path: '',    component: BookingListComponent, canActivate: [AuthGuard] },
  { path: 'new', component: BookingFormComponent, canActivate: [AuthGuard] }
];
