import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AdminUser, ClienteUser } from '../models';

const API = 'http://localhost:8080';

@Injectable({ providedIn: 'root' })
export class UserManagementService {

  constructor(private http: HttpClient) {}

  // ── ADMINS ─────────────────────────────────────────────────────────────────

  getAdmins(): Observable<AdminUser[]> {
    // El backend devuelve 204 (sin cuerpo) si la lista está vacía → tratar como []
    return this.http.get<AdminUser[]>(`${API}/admin/mostrartodo`).pipe(
      catchError(() => of([] as AdminUser[]))
    );
  }

  createAdmin(nombreUsuario: string, contraseniaUsuario: string): Observable<string> {
    const params = new HttpParams()
      .set('nombreUsuario', nombreUsuario)
      .set('contraseniaUsuario', contraseniaUsuario);
    return this.http.post(`${API}/admin/crear`, null, { params, responseType: 'text' });
  }

  deleteAdmin(id: number): Observable<string> {
    return this.http.delete(`${API}/admin/eliminar/${id}`, { responseType: 'text' });
  }

  // ── CLIENTES ──────────────────────────────────────────────────────────────

  getClientes(): Observable<ClienteUser[]> {
    return this.http.get<ClienteUser[]>(`${API}/cliente/mostrartodo`).pipe(
      catchError(() => of([] as ClienteUser[]))
    );
  }

  deleteCliente(id: number): Observable<string> {
    return this.http.delete(`${API}/cliente/eliminar/${id}`, { responseType: 'text' });
  }
}
