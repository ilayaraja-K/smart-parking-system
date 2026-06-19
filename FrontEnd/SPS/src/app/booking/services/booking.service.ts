import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface ParkingSlot {
  id?: number;
  slotId?: number;            // actual JSON field from backend
  slotNumber: string;
  vehicleType: 'CAR' | 'BIKE';
  buildingId?: number;
  status?: string;
  available?: boolean;
  isAvailable?: boolean;
}

export interface Booking {
  id?: number;
  userId?: number;
  vehicleId: number;
  slotId: number;
  buildingId?: number;
  slot?: ParkingSlot;
  buildingName?: string;
  startTime?: string;
  endTime?: string;
  status: 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  duration?: string;
}

export interface BookingRequest {
  vehicleId: number;
  slotId: number;
}

function unwrap<T>(res: any): T {
  return res?.appResponse ?? res;
}

@Injectable({ providedIn: 'root' })
export class BookingService {
  private readonly base = 'http://localhost:8091/pabsm';

  constructor(private http: HttpClient) {}

  getMyBookings(userId: number): Observable<Booking[]> {
    return this.http.get<any>(`${this.base}/users/${userId}/bookings`)
      .pipe(map(res => unwrap<Booking[]>(res)));
  }

  getAvailableSlots(buildingId: number, vehicleType: string): Observable<ParkingSlot[]> {
    const params = new HttpParams().set('vehicleType', vehicleType);
    return this.http.get<any>(`${this.base}/slots/building/${buildingId}/available`, { params })
      .pipe(map(res => unwrap<ParkingSlot[]>(res)));
  }

  create(booking: BookingRequest): Observable<Booking> {
    return this.http.post<any>(`${this.base}/bookings`, booking)
      .pipe(map(res => unwrap<Booking>(res)));
  }

  complete(id: number): Observable<Booking> {
    return this.http.put<any>(`${this.base}/bookings/${id}/complete`, {})
      .pipe(map(res => unwrap<Booking>(res)));
  }

  cancel(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/bookings/${id}`);
  }

  getAll(): Observable<Booking[]> {
    return this.http.get<any>(`${this.base}/bookings`)
      .pipe(map(res => unwrap<Booking[]>(res)));
  }

  getSlotsByBuilding(buildingId: number): Observable<ParkingSlot[]> {
    return this.http.get<any>(`${this.base}/slots/building/${buildingId}`)
      .pipe(map(res => unwrap<ParkingSlot[]>(res)));
  }

  createSlot(slot: { buildingId: number; slotNumber: string; vehicleType: string }): Observable<ParkingSlot> {
    return this.http.post<any>(`${this.base}/slots`, slot)
      .pipe(map(res => unwrap<ParkingSlot>(res)));
  }
}
