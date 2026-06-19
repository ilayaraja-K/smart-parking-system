import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthGuard } from './auth/guards/auth.guard';
import { ProfileComponent } from './profile/profile.component';

export const routes: Routes = [
  { path: '', redirectTo: '/auth/login', pathMatch: 'full' },
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.routes').then(m => m.authRoutes)
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'vehicles',
    loadChildren: () => import('./vehicle/vehicle.routes').then(m => m.vehicleRoutes)
  },
  {
    path: 'bookings',
    loadChildren: () => import('./booking/booking.routes').then(m => m.bookingRoutes)
  },
  {
    path: 'admin',
    loadChildren: () => import('./admin/admin.routes').then(m => m.adminRoutes)
  },
  {
    path: 'buildings',
    loadChildren: () => import('./building/building.routes').then(m => m.buildingRoutes)
  },
  { path: '**', redirectTo: '/auth/login' }
];
