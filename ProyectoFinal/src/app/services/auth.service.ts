import { Injectable, signal, computed } from '@angular/core';
import { User } from '../models';

export interface ManagedUser {
  id: number;
  name: string;
  email: string;
  password: string;
  role: 'admin' | 'client';
  joinDate: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private _user = signal<User | null>(null);
  readonly user = this._user.asReadonly();
  readonly isLoggedIn = computed(() => this._user() !== null);
  readonly isAdmin    = computed(() => this._user()?.role === 'admin');

  private _users = signal<ManagedUser[]>([
    { id: 1, name: 'Admin UniShop',  email: 'admin@unishop.co',  password: 'Admin123!', role: 'admin'  as const, joinDate: '2024-01-01' },
    { id: 2, name: 'Juan García',    email: 'juan@unishop.co',   password: 'Cliente1!', role: 'client' as const, joinDate: '2025-03-15' },
    { id: 3, name: 'Demo Cliente',   email: 'demo@unishop.co',   password: 'Demo1234!', role: 'client' as const, joinDate: '2025-06-10' },
  ]);

  readonly users = this._users.asReadonly();

  login(email: string, password: string): { success: boolean; message: string } {
    const found = this._users().find(u => u.email === email && u.password === password);
    if (!found) return { success: false, message: 'Credenciales inválidas' };
    const { password: _, ...user } = found;
    this._user.set(user);
    return { success: true, message: `Bienvenido, ${user.name}` };
  }

  loginAsAdmin()  { this.login('admin@unishop.co', 'Admin123!'); }
  loginAsClient() { this.login('demo@unishop.co',  'Demo1234!'); }
  logout()        { this._user.set(null); }

  // ── Gestión de usuarios ───────────────────────────────────────────────

  createUser(name: string, email: string, password: string, role: 'admin' | 'client'): { success: boolean; message: string } {
    if (this._users().some(u => u.email === email)) {
      return { success: false, message: 'El correo ya está en uso' };
    }
    const newUser: ManagedUser = {
      id: Math.max(...this._users().map(u => u.id)) + 1,
      name, email, password, role,
      joinDate: new Date().toISOString().split('T')[0]
    };
    this._users.update(list => [...list, newUser]);
    return { success: true, message: `Usuario "${name}" creado correctamente` };
  }

  updateUser(id: number, name: string, email: string, password: string, role: 'admin' | 'client'): { success: boolean; message: string } {
    const conflict = this._users().find(u => u.email === email && u.id !== id);
    if (conflict) return { success: false, message: 'El correo ya está en uso por otro usuario' };
    this._users.update(list =>
      list.map(u => u.id === id ? { ...u, name, email, password, role } : u)
    );
    return { success: true, message: `Usuario actualizado correctamente` };
  }

  deleteUser(id: number): { success: boolean; message: string } {
    if (this._user()?.id === id) return { success: false, message: 'No puedes eliminar tu propia cuenta' };
    this._users.update(list => list.filter(u => u.id !== id));
    return { success: true, message: 'Usuario eliminado correctamente' };
  }
}
