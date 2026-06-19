import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMsg = '';
  loading = false;
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email:    ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  get emailCtrl()    { return this.loginForm.get('email')!; }
  get passwordCtrl() { return this.loginForm.get('password')!; }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading  = true;
    this.errorMsg = '';
    const { email, password } = this.loginForm.value;

    // Calls POST http://localhost:8090/uvmgmt/users/login
    // Stores JWT in localStorage under key 'sps_user'
    this.authService.login(email, password).subscribe({
      next: (user) => {
        this.loading = false;
        // Redirect based on role
        this.router.navigate(user.role === 'admin' ? ['/admin'] : ['/dashboard']);
      },
      error: (err) => {
        this.loading  = false;
        this.errorMsg = err?.error?.message
          || err?.error?.error
          || 'Invalid email or password. Please try again.';
      }
    });
  }
}
