import { Routes } from '@angular/router';
import { VehicleListComponent } from './components/vehicle-list/vehicle-list.component';
import { VehicleFormComponent } from './components/vehicle-form/vehicle-form.component';
import { AuthGuard } from '../auth/guards/auth.guard';

export const vehicleRoutes: Routes = [
  { path: '', component: VehicleListComponent, canActivate: [AuthGuard] },
  { path: 'add', component: VehicleFormComponent, canActivate: [AuthGuard] },
  { path: 'edit/:id', component: VehicleFormComponent, canActivate: [AuthGuard] }
];
