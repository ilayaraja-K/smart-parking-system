import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { VehicleService } from '../../services/vehicle.service';
import { AuthService } from '../../../auth/services/auth.service';

function extractError(err: unknown): string {
  const e = err as { error?: { message?: string; error?: string } | string; message?: string };
  if (typeof e?.error === 'string') return e.error;
  if (typeof e?.error?.message === 'string') return e.error.message;
  if (typeof e?.error?.error === 'string') return e.error.error;
  return 'An error occurred. Please try again.';
}

@Component({
  selector: 'app-vehicle-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './vehicle-form.component.html'
})
export class VehicleFormComponent implements OnInit {
  vehicleForm: FormGroup;
  isEdit = false;
  vehicleId: number | null = null;
  loading = false;
  errorMsg = '';
  successMsg = '';

  constructor(
    private fb: FormBuilder,
    private vehicleService: VehicleService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.vehicleForm = this.fb.group({
      vehicleNumber: ['', [Validators.required, Validators.pattern(/^[A-Z0-9-]+$/i)]],
      vehicleType:   ['CAR', Validators.required],
      model:         ['', Validators.required]
    });
  }

  get vehicleNumberCtrl() { return this.vehicleForm.get('vehicleNumber')!; }
  get modelCtrl()         { return this.vehicleForm.get('model')!; }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit    = true;
      this.vehicleId = Number(id);
      this.vehicleService.getById(this.vehicleId).subscribe({
        next: (v) => this.vehicleForm.patchValue(v),
        error: () => this.errorMsg = 'Failed to load vehicle data.'
      });
    }
  }

  onSubmit(): void {
    if (this.vehicleForm.invalid) {
      this.vehicleForm.markAllAsTouched();
      return;
    }

    this.loading    = true;
    this.errorMsg   = '';
    this.successMsg = '';

    const userId = this.authService.currentUser?.id ?? 0;
    if (!this.isEdit && !userId) {
      this.errorMsg = 'Cannot determine user ID. Please log out and log in again.';
      this.loading = false;
      return;
    }

    if (this.isEdit) {
      // PUT /vehicles/{id}
      this.vehicleService.update(this.vehicleId!, this.vehicleForm.value).subscribe({
        next: () => {
          this.loading    = false;
          this.successMsg = 'Vehicle updated successfully!';
          setTimeout(() => this.router.navigate(['/vehicles']), 1000);
        },
        error: (err) => {
          this.loading  = false;
          this.errorMsg = extractError(err);
        }
      });
    } else {
      // POST /vehicles/register
      const payload = { ...this.vehicleForm.value, userId };
      this.vehicleService.register(payload).subscribe({
        next: () => {
          this.loading    = false;
          this.successMsg = 'Vehicle registered successfully!';
          setTimeout(() => this.router.navigate(['/vehicles']), 1000);
        },
        error: (err) => {
          this.loading  = false;
          this.errorMsg = extractError(err);
        }
      });
    }
  }
}

