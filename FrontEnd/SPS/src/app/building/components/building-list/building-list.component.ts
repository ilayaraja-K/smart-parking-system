import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Building, getBuildingName } from '../../services/building.service';
import { debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-building-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './building-list.component.html'
})
export class BuildingListComponent implements OnInit {
  buildings: Building[] = [];
  filtered: Building[] = [];
  loading = false;
  errorMsg = '';
  searchCtrl = new FormControl('');

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadBuildings();
    this.searchCtrl.valueChanges.pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(term => { this.applyFilter(term ?? ''); this.cdr.detectChanges(); });
  }

  loadBuildings(): void {
    this.loading  = true;
    this.errorMsg = '';
    this.cdr.detectChanges();

    let url = 'http://localhost:8091/pabsm/buildings';
    const city = this.searchCtrl.value?.trim();
    if (city) url += `?city=${encodeURIComponent(city)}`;

    this.http.get<any>(url).subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.buildings = Array.isArray(data) ? data : [];
        this.filtered  = this.buildings;
        this.loading   = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMsg = `Error ${err?.status ?? 'unknown'}: Failed to load buildings.`;
        this.loading  = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyFilter(term: string): void {
    const q = term.trim().toLowerCase();
    this.filtered = q
      ? this.buildings.filter(b => b.city.toLowerCase().includes(q) || getBuildingName(b).toLowerCase().includes(q))
      : this.buildings;
  }

  getBuildingName(b: Building): string { return getBuildingName(b); }

  clearSearch(): void { this.searchCtrl.setValue(''); }

  get uniqueCities(): string[] {
    return [...new Set(this.buildings.map(b => b.city))].sort();
  }

  filterByCity(city: string): void { this.searchCtrl.setValue(city); }
}
