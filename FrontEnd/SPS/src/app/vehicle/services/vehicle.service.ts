import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export type VehicleType = 'CAR' | 'BIKE';

export interface Vehicle {
  id?: number;
  userId?: number;
  vehicleNumber: string;
  vehicleType: VehicleType;
  model: string;
}

@Injectable({ providedIn: 'root' })
export class VehicleService {
  private readonly base = 'https://myapp-service-s92w.onrender.com/uvmgmt/vehicles';

  constructor(private http: HttpClient) {}

  getByUser(userId: number): Observable<Vehicle[]> {
    return this.http.get<any>(`${this.base}/user/${userId}`).pipe(
      map(res => {
        console.log('[VehicleService] raw:', res);
        // Backend wraps in appResponse
        const data = res?.appResponse ?? res;
        return Array.isArray(data) ? data as Vehicle[] : [];
      }),
      catchError(err => {
        console.error('[VehicleService] error:', err);
        throw err;
      })
    );
  }

  getById(id: number): Observable<Vehicle> {
    return this.http.get<any>(`${this.base}/${id}`).pipe(
      map(res => (res?.appResponse ?? res) as Vehicle)
    );
  }

  register(vehicle: { vehicleNumber: string; vehicleType: VehicleType; model: string; userId: number }): Observable<Vehicle> {
    return this.http.post<any>(`${this.base}/register`, vehicle).pipe(
      map(res => (res?.appResponse ?? res) as Vehicle)
    );
  }

  update(id: number, vehicle: Partial<Vehicle>): Observable<Vehicle> {
    return this.http.put<any>(`${this.base}/${id}`, vehicle).pipe(
      map(res => (res?.appResponse ?? res) as Vehicle)
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

