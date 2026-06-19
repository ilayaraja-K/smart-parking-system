import { Routes } from '@angular/router';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { AdminBookingsComponent } from './components/admin-bookings/admin-bookings.component';
import { AdminSlotsComponent } from './components/admin-slots/admin-slots.component';
import { AdminUsersComponent } from './components/admin-users/admin-users.component';
import { AdminCreateBuildingComponent } from './components/admin-create-building/admin-create-building.component';
import { AdminGuard } from '../auth/guards/auth.guard';

export const adminRoutes: Routes = [
  {
    path: '',
    canActivate: [AdminGuard],
    children: [
      { path: '', component: AdminDashboardComponent },
      { path: 'bookings', component: AdminBookingsComponent },
      { path: 'slots', component: AdminSlotsComponent },
      { path: 'users', component: AdminUsersComponent },
      { path: 'buildings/create', component: AdminCreateBuildingComponent }
    ]
  }
];
