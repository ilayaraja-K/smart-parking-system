import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface Building {
  id?: number;
  buildingId?: number;        // actual JSON field from backend
  name?: string;
  buildingName?: string;
  city: string;
  address?: string;
  totalSlots?: number;
  availableSlots?: number;
}

export function getBuildingName(b: Building): string {
  return b.name ?? b.buildingName ?? '';
}

function unwrap<T>(res: any): T {
  return res?.appResponse ?? res;
}

@Injectable({ providedIn: 'root' })
export class BuildingService {
  private readonly baseUrl = 'http://localhost:8091/pabsm/buildings';

  constructor(private http: HttpClient) {}

  getAll(city?: string): Observable<Building[]> {
    let params = new HttpParams();
    if (city) params = params.set('city', city);
    return this.http.get<any>(this.baseUrl, { params })
      .pipe(map(res => unwrap<Building[]>(res)));
  }

  getById(id: number): Observable<Building> {
    return this.http.get<any>(`${this.baseUrl}/${id}`)
      .pipe(map(res => unwrap<Building>(res)));
  }

  create(building: { name: string; city: string }): Observable<Building> {
    return this.http.post<any>(this.baseUrl, building)
      .pipe(map(res => unwrap<Building>(res)));
  }
}
