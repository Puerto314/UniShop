import { Injectable, signal, computed } from '@angular/core';
import { User } from '../models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private _user = signal<User | null>(null);
  readonly user = this._user.asReadonly();
  readonly isLoggedIn = computed(() => this._user() !== null);
  readonly isAdmin    = computed(() => this._user()?.role === 'admin');

  private readonly USERS = [
    { id: 1, name: 'Admin NeonMarket', email: 'admin@neon.co',   password: 'Admin123!', role: 'admin'  as const, joinDate: '2024-01-01' },
    { id: 2, name: 'Juan García',      email: 'juan@neon.co',     password: 'Cliente1!', role: 'client' as const, joinDate: '2025-03-15' },
    { id: 3, name: 'Demo Cliente',     email: 'demo@neon.co',     password: 'Demo1234!', role: 'client' as const, joinDate: '2025-06-10' },
  ];

  login(email: string, password: string): { success: boolean; message: string } {
    const found = this.USERS.find(u => u.email === email && u.password === password);
    if (!found) return { success: false, message: 'Credenciales inválidas' };
    const { password: _, ...user } = found;
    this._user.set(user);
    return { success: true, message: `Bienvenido, ${user.name}` };
  }

  loginAsAdmin()  { this.login('admin@neon.co', 'Admin123!'); }
  loginAsClient() { this.login('demo@neon.co',  'Demo1234!'); }
  logout()        { this._user.set(null); }
}
