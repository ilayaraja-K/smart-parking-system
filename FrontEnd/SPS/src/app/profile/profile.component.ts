import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
<div class="container py-5" style="max-width: 540px;">
  <div class="card shadow-sm border-0">
    <div class="card-header bg-primary text-white">
      <h5 class="mb-0"><i class="bi bi-person-fill me-2"></i>My Profile</h5>
    </div>
    <div class="card-body p-4">

      <div *ngIf="loading" class="text-center py-4">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="mt-2 text-muted">Loading profile…</p>
      </div>

      <form *ngIf="!loading" (ngSubmit)="save()" #profileForm="ngForm">

        <div *ngIf="successMsg" class="alert alert-success py-2">
          <i class="bi bi-check-circle me-1"></i>{{ successMsg }}
        </div>
        <div *ngIf="errorMsg" class="alert alert-danger py-2">
          <i class="bi bi-exclamation-circle me-1"></i>{{ errorMsg }}
        </div>

        <div class="mb-3">
          <label class="form-label fw-semibold">Full Name</label>
          <input type="text" class="form-control" [(ngModel)]="form.name" name="name" required placeholder="Your name" />
        </div>

        <div class="mb-3">
          <label class="form-label fw-semibold">Email Address</label>
          <input type="email" class="form-control" [(ngModel)]="form.email" name="email" required placeholder="you@example.com" />
        </div>

        <div class="mb-4">
          <label class="form-label fw-semibold">
            New Password <span class="text-muted fw-normal">(leave blank to keep current)</span>
          </label>
          <input type="password" class="form-control" [(ngModel)]="form.password" name="password" placeholder="••••••••" />
        </div>

        <div class="d-grid">
          <button type="submit" class="btn btn-primary" [disabled]="saving || profileForm.invalid">
            <span *ngIf="saving" class="spinner-border spinner-border-sm me-2"></span>
            {{ saving ? 'Saving…' : 'Save Changes' }}
          </button>
        </div>

      </form>
    </div>
  </div>
</div>
  `
})
export class ProfileComponent implements OnInit {

  loading = true;
  saving  = false;
  successMsg = '';
  errorMsg   = '';

  form = { name: '', email: '', password: '' };

  private userId!: number;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUser;
    if (!user) { this.errorMsg = 'Not authenticated.'; this.loading = false; return; }
    this.userId = user.id;

    this.http.get<any>(`https://myapp-service-s92w.onrender.com/uvmgmt/users/${this.userId}`).subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        this.form.name  = data?.name  ?? user.name;
        this.form.email = data?.email ?? user.email;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        // fallback to stored values
        this.form.name  = user.name;
        this.form.email = user.email;
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  save(): void {
    this.saving     = true;
    this.successMsg = '';
    this.errorMsg   = '';

const body: any = {
  name: this.form.name
};

if (this.form.password?.trim()) {
  body.password = this.form.password.trim();
}
    this.http.put<any>(`https://myapp-service-s92w.onrender.com/uvmgmt/users/${this.userId}`, body).subscribe({
      next: (res) => {
        const data = res?.appResponse ?? res;
        // refresh stored auth user with new name/email
        this.authService.refreshStoredUser({ name: data?.name ?? this.form.name, email: data?.email ?? this.form.email });
        this.form.password = '';
        this.successMsg = 'Profile updated successfully!';
        this.saving = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMsg = err?.error?.message?.responseMessage ?? 'Failed to update profile.';
        this.saving = false;
        this.cdr.detectChanges();
      }
    });
  }
}
