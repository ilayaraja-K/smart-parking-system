import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

export interface AuthUser {
  id: number;
  name: string;
  email: string;
  role: 'admin' | 'user';
  token: string;
}

/** Decode JWT payload — no external library needed */
function decodeJwtPayload(token: string): Record<string, unknown> {
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
  } catch { return {}; }
}

/** Extract numeric user id — tries all common JWT claim names */
function extractUserId(payload: Record<string, unknown>): number {
  const val = payload['userId'] ?? payload['user_id'] ?? payload['id'];
  if (typeof val === 'number' && val > 0) return val;
  if (typeof val === 'string' && Number(val) > 0) return Number(val);
  return 0;
}

/** Extract role — handles uppercase USER/ADMIN from Spring Boot */
function extractRole(payload: Record<string, unknown>, resRole?: string): 'admin' | 'user' {
  const raw = resRole ?? payload['role'] ?? payload['roles'] ?? payload['authority'] ?? 'user';
  return String(raw).toUpperCase() === 'ADMIN' ? 'admin' : 'user';
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly loginUrl    = 'https://myapp-service-s92w.onrender.com/uvmgmt/users/login';
  private readonly registerUrl = 'https://myapp-service-s92w.onrender.com/uvmgmt/users/register';
  private readonly storageKey  = 'sps_user';
  private readonly isBrowser   = isPlatformBrowser(inject(PLATFORM_ID));

  private _user$ = new BehaviorSubject<AuthUser | null>(null);
  currentUser$   = this._user$.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    if (this.isBrowser) {
      const raw = localStorage.getItem(this.storageKey);
      if (raw) {
        try {
          const stored: AuthUser = JSON.parse(raw);
          // Discard if token is missing or id is 0
          if (!stored.token || stored.token === 'undefined') {
            localStorage.removeItem(this.storageKey);
          } else if (!stored.id || stored.id === 0) {
            // Try to recover id from the stored JWT
            const payload = decodeJwtPayload(stored.token);
            const fixedId = extractUserId(payload);
            if (fixedId > 0) {
              stored.id   = fixedId;
              stored.role = extractRole(payload);
              localStorage.setItem(this.storageKey, JSON.stringify(stored));
              this._user$.next(stored);
            } else {
              localStorage.removeItem(this.storageKey);
            }
          } else {
            this._user$.next(stored);
          }
        } catch { localStorage.removeItem(this.storageKey); }
      }
    }
  }

  login(email: string, password: string): Observable<AuthUser> {
    return this.http.post<any>(this.loginUrl, { email, password }).pipe(
      map(res => {
        // Backend: { message: {...}, appResponse: { token: "eyJ..." } }
        const token: string = res?.appResponse?.token ?? res?.token ?? '';
        if (!token) throw new Error('No token received from server');

        const payload = decodeJwtPayload(token);
        console.log('[Login] JWT payload:', payload);

        const user: AuthUser = {
          // All user info comes from JWT since appResponse only contains token
          id:    extractUserId(payload),
          name:  (payload['name'] as string) ?? (payload['sub'] as string ?? '').split('@')[0] ?? email.split('@')[0],
          email: (payload['sub'] as string) ?? email,
          role:  extractRole(payload),
          token
        };
        console.log('[Login] AuthUser built:', user);
        return user;
      }),
      tap(user => {
        if (this.isBrowser) localStorage.setItem(this.storageKey, JSON.stringify(user));
        this._user$.next(user);
      })
    );
  }

  register(name: string, email: string, password: string): Observable<AuthUser> {
    return this.http.post<any>(this.registerUrl, { name, email, password }).pipe(
      map(res => {
        const token: string = res?.appResponse?.token ?? res?.token ?? '';
        if (!token) throw new Error('No token received from server');

        const payload = decodeJwtPayload(token);
        const user: AuthUser = {
          id:    extractUserId(payload),
          name:  (payload['name'] as string) ?? name,
          email: (payload['sub'] as string) ?? email,
          role:  extractRole(payload),
          token
        };
        return user;
      }),
      tap(user => {
        if (this.isBrowser) localStorage.setItem(this.storageKey, JSON.stringify(user));
        this._user$.next(user);
      })
    );
  }

  logout(): void {
    if (this.isBrowser) localStorage.removeItem(this.storageKey);
    this._user$.next(null);
    this.router.navigate(['/auth/login']);
  }

  /** Update name/email in stored session after profile edit */
  refreshStoredUser(patch: { name?: string; email?: string }): void {
    const user = this._user$.value;
    if (!user) return;
    const updated = { ...user, ...patch };
    if (this.isBrowser) localStorage.setItem(this.storageKey, JSON.stringify(updated));
    this._user$.next(updated);
  }

  get currentUser(): AuthUser | null { return this._user$.value; }
  get isLoggedIn():  boolean         { return !!this._user$.value; }
  get isAdmin():     boolean         { return this._user$.value?.role === 'admin'; }
  getToken():        string | null   { return this._user$.value?.token ?? null; }
  get displayFirstName(): string {
  const name = this.currentUser?.name ?? '';
  return name.split(' ')[0];
}
}

export interface LoginRequest  { email: string; password: string; }
export interface LoginResponse { token: string; id?: number; name?: string; email?: string; role?: 'admin' | 'user'; }
export interface AuthUser {
  id: number;
  name: string;
  email: string;
  role: 'admin' | 'user';
  token: string;
}

