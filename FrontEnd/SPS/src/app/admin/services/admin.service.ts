import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Building } from '../../building/services/building.service';
import { Booking, ParkingSlot } from '../../booking/services/booking.service';

export interface AppUser {
  id: number;
  name: string;
  email: string;
  role: string;
}

function unwrap<T>(res: any): T {
  return res?.appResponse ?? res;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly pabsm  = 'http://localhost:8091/pabsm';
  private readonly uvmgmt = 'http://localhost:8090/uvmgmt';

  constructor(private http: HttpClient) {}

  getAllBuildings(): Observable<Building[]> {
    return this.http.get<any>(`${this.pabsm}/buildings`).pipe(map(r => unwrap<Building[]>(r)));
  }

  createBuilding(body: { name: string; city: string }): Observable<Building> {
    return this.http.post<any>(`${this.pabsm}/buildings`, body).pipe(map(r => unwrap<Building>(r)));
  }

  getSlotsByBuilding(buildingId: number): Observable<ParkingSlot[]> {
    return this.http.get<any>(`${this.pabsm}/slots/building/${buildingId}`).pipe(map(r => unwrap<ParkingSlot[]>(r)));
  }

  createSlot(body: { buildingId: number; slotNumber: string; vehicleType: string }): Observable<ParkingSlot> {
    return this.http.post<any>(`${this.pabsm}/slots`, body).pipe(map(r => unwrap<ParkingSlot>(r)));
  }

  getAllBookings(): Observable<Booking[]> {
    return this.http.get<any>(`${this.pabsm}/bookings`).pipe(map(r => unwrap<Booking[]>(r)));
  }

  getAllUsers(): Observable<AppUser[]> {
    return this.http.get<any>(`${this.uvmgmt}/users`).pipe(map(r => unwrap<AppUser[]>(r)));
  }
}

