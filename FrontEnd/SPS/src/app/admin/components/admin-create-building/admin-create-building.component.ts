import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-admin-create-building',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './admin-create-building.component.html'
})
export class AdminCreateBuildingComponent {
  form: FormGroup;
  submitting = false;
  successMsg = '';
  errorMsg = '';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.form = this.fb.group({
      name:    ['', Validators.required],
      city:    ['', Validators.required],
      address: ['']
    });
  }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.submitting = true;
    this.errorMsg   = '';
    this.cdr.detectChanges();
    this.http.post<any>('https://parking-service-un8u.onrender.com/pabsm/buildings', this.form.value).subscribe({
      next: () => {
        this.successMsg = 'Building created successfully!';
        this.submitting = false;
        this.cdr.detectChanges();
        setTimeout(() => this.router.navigate(['/admin']), 1500);
      },
      error: (err) => {
        this.errorMsg   = err?.error?.message || 'Failed to create building.';
        this.submitting = false;
        this.cdr.detectChanges();
      }
    });
  }
}
