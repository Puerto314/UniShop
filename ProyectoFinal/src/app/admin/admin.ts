import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../services/product.service';
import { OrderService } from '../services/order.service';
import { AuthService, ManagedUser } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin {
  activeTab = signal<'dashboard'|'products'|'orders'|'users'>('dashboard');

  // User form state
  showForm    = signal(false);
  editingUser: ManagedUser | null = null;
  formName    = '';
  formEmail   = '';
  formPassword = '';
  formRole: 'admin' | 'client' = 'client';
  formError   = '';

  constructor(
    public products: ProductService,
    public orders: OrderService,
    public auth: AuthService,
    private notify: NotificationService,
    private router: Router,
  ) {}

  get totalRevenue() { return this.orders.orders().reduce((s,o) => s + o.total, 0); }
  get totalOrders()  { return this.orders.orders().length; }
  get totalProducts(){ return this.products.PRODUCTS.length; }
  get totalUsers()   { return this.auth.users().length; }

  formatPrice(p: number) { return '$' + p.toLocaleString('es-CO'); }
  formatDate(d: string)  { return new Date(d).toLocaleDateString('es-CO', { month:'short', day:'numeric' }); }

  logout() {
    this.auth.logout();
    this.notify.info('Sesión cerrada.');
    this.router.navigate(['/login']);
  }

  // ── User CRUD ──────────────────────────────────────────────────────────

  openCreateForm() {
    this.editingUser = null;
    this.formName = ''; this.formEmail = ''; this.formPassword = ''; this.formRole = 'client';
    this.formError = '';
    this.showForm.set(true);
  }

  openEditForm(u: ManagedUser) {
    this.editingUser = u;
    this.formName = u.name; this.formEmail = u.email;
    this.formPassword = u.password; this.formRole = u.role;
    this.formError = '';
    this.showForm.set(true);
  }

  cancelForm() { this.showForm.set(false); this.editingUser = null; this.formError = ''; }

  saveUser() {
    this.formError = '';
    if (!this.formName.trim() || !this.formEmail.trim() || !this.formPassword.trim()) {
      this.formError = 'Todos los campos son obligatorios.'; return;
    }
    if (this.formPassword.length < 6) {
      this.formError = 'La contraseña debe tener al menos 6 caracteres.'; return;
    }

    let result: { success: boolean; message: string };
    if (this.editingUser) {
      result = this.auth.updateUser(this.editingUser.id, this.formName, this.formEmail, this.formPassword, this.formRole);
    } else {
      result = this.auth.createUser(this.formName, this.formEmail, this.formPassword, this.formRole);
    }

    if (result.success) {
      this.notify.success(result.message);
      this.cancelForm();
    } else {
      this.formError = result.message;
    }
  }

  deleteUser(id: number) {
    const result = this.auth.deleteUser(id);
    result.success ? this.notify.success(result.message) : this.notify.error(result.message);
  }
}
