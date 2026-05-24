import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AdminUser, ClienteUser} from '../models';

const API = 'http://localhost:8080';

@Injectable({providedIn: 'root'})
export class UserManagementService {

  constructor(private http: HttpClient) {
  }

  // ── ADMINS ─────────────────────────────────────────────────────────────────

  getAdmins(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${API}/admin/mostrartodo`);
  }

  createAdmin(nombreUsuario: string, contraseniaUsuario: string): Observable<string> {
    const params = new HttpParams()
      .set('nombreUsuario', nombreUsuario)
      .set('contraseniaUsuario', contraseniaUsuario);
    return this.http.post(`${API}/admin/crear`, null, {params, responseType: 'text'});
  }

  deleteAdmin(id: number): Observable<string> {
    return this.http.delete(`${API}/admin/eliminar/${id}`, {responseType: 'text'});
  }

  // ── CLIENTES ──────────────────────────────────────────────────────────────

  getClientes(): Observable<ClienteUser[]> {
    return this.http.get<ClienteUser[]>(`${API}/cliente/mostrartodo`);
  }

  deleteCliente(id: number): Observable<string> {
    return this.http.delete(`${API}/cliente/eliminar/${id}`, {responseType: 'text'});
  }
}
