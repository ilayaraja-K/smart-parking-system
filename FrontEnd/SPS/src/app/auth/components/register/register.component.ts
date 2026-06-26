import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMsg = '';
successMsg = '';
loading = false;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    const p = form.get('password')?.value;
    const cp = form.get('confirmPassword')?.value;
    return p === cp ? null : { passwordMismatch: true };
  }

onSubmit(): void {

  if (this.registerForm.invalid) return;

  this.loading = true;
  this.errorMsg = '';
  this.successMsg = '';

  const { name, email, password } = this.registerForm.value;

  this.authService.register(name, email, password).subscribe({

    next: () => {

      this.loading = false;

      this.successMsg = 'Email registered successfully. Redirecting to Login...';

      setTimeout(() => {
        this.router.navigate(['/auth/login']);
      }, 1500);

    },

    error: (err) => {

      this.loading = false;

      this.errorMsg =
        err?.error?.appResponse ??
        'Registration failed. Please try again.';

    }

  });

}
}
