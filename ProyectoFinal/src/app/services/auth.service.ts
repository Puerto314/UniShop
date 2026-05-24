import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { User } from '../models';

const API_URL = 'https://gpcueb.com/unishop';
const TOKEN_KEY = 'unishop_token';
const USER_KEY  = 'unishop_user';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private _user = signal<User | null>(this.loadUserFromStorage());
  readonly user       = this._user.asReadonly();
  readonly isLoggedIn = computed(() => this._user() !== null);
  readonly isAdmin    = computed(() => this._user()?.rol === 'ROLE_ADMIN');

  constructor(private http: HttpClient) {}

  // ── Helpers de almacenamiento ──────────────────────────────────────────────

  private loadUserFromStorage(): User | null {
    try {
      const raw = sessionStorage.getItem(USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch { return null; }
  }

  getToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  private saveSession(token: string, user: User): void {
    sessionStorage.setItem(TOKEN_KEY, token);
    sessionStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  private clearSession(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(USER_KEY);
  }

  // ── Login (llama al backend real) ─────────────────────────────────────────

  loginBackend(nombreUsuario: string, contrasenia: string): Observable<any> {
    return this.http.post<{ token: string; nombreUsuario: string; rol: string }>(
      `${API_URL}/auth/login`,
      { nombreUsuario, contraseniaUsuario: contrasenia }
    ).pipe(
      tap(res => {
        const user: User = { nombreUsuario: res.nombreUsuario, rol: res.rol };
        this.saveSession(res.token, user);
        this._user.set(user);
      }),
      catchError(err => throwError(() => err))
    );
  }

  // ── Registro: paso 1 — solicitar código ──────────────────────────────────

  enviarCodigo(nombreUsuario: string, contrasenia: string, correo: string): Observable<any> {
    return this.http.post<any>(
      `${API_URL}/auth/enviar-codigo`,
      { nombreUsuario, contraseniaUsuario: contrasenia, correoElectronico: correo }
    ).pipe(catchError(err => throwError(() => err)));
  }

  // ── Registro: paso 2 — verificar código y crear cuenta ───────────────────

  verificarYRegistrar(nombreUsuario: string, contrasenia: string, correo: string, codigo: string): Observable<any> {
    return this.http.post<any>(
      `${API_URL}/auth/verificar-y-registrar`,
      { nombreUsuario, contraseniaUsuario: contrasenia, correoElectronico: correo, codigo }
    ).pipe(catchError(err => throwError(() => err)));
  }

  // ── Logout ────────────────────────────────────────────────────────────────

  logout(): void {
    this.clearSession();
    this._user.set(null);
  }
}
