import { Routes } from '@angular/router';
import { BuildingListComponent } from './components/building-list/building-list.component';
import { AuthGuard } from '../auth/guards/auth.guard';

export const buildingRoutes: Routes = [
  { path: '', component: BuildingListComponent, canActivate: [AuthGuard] }
];
