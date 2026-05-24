import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../services/product.service';
import { OrderService } from '../services/order.service';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
import { UserManagementService } from '../services/user-management.service';
import { AdminUser, ClienteUser } from '../models';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin implements OnInit {
  activeTab = signal<'dashboard' | 'orders' | 'users'>('dashboard');

  // ── Datos usuarios ────────────────────────────────────────────────────────
  admins: AdminUser[] = [];
  clientes: ClienteUser[] = []
  userSubTab: 'admins' | 'clientes' = 'admins';

  // ── Formulario nuevo admin ─────────────────────────────────────────────────
  newAdminUsername = '';
  newAdminPassword = '';
  loadingUsers = false;
  loadingOrders = false;

  constructor(
    public products: ProductService,
    public orders: OrderService,
    public auth: AuthService,
    private notify: NotificationService,
    private router: Router,
    private userMgmt: UserManagementService,
  ) {}

  ngOnInit() {}

  // ── Pestaña Historial Global ──────────────────────────────────────────────

  openOrdersTab() {
    this.activeTab.set('orders');
    this.loadingOrders = true;
    this.orders.loadTodasLasOrdenes().subscribe({
      next: () => (this.loadingOrders = false),
      error: () => {
        this.notify.error('Error cargando el historial de pedidos');
        this.loadingOrders = false;
      },
    });
  }

  // ── Pestaña Usuarios ──────────────────────────────────────────────────────

  openUsersTab() {
    this.activeTab.set('users');
    this.loadUsers();
  }

  loadUsers() {
    this.loadingUsers = true;
    this.userMgmt.getAdmins().subscribe({
      next: data => {
        this.admins = data ?? [];
        this.loadingUsers = false;
      },
      error: () => {
        this.notify.error('Error cargando admins');
        this.loadingUsers = false;
      },
    });
    this.userMgmt.getClientes().subscribe({
      next: data => (this.clientes = data ?? []),
      error: () => this.notify.error('Error cargando clientes'),
    });
  }

  createAdmin() {
    if (!this.newAdminUsername || !this.newAdminPassword) {
      this.notify.error('Completa usuario y contraseña');
      return;
    }
    this.userMgmt.createAdmin(this.newAdminUsername, this.newAdminPassword).subscribe({
      next: () => {
        this.notify.success('Admin creado');
        this.newAdminUsername = '';
        this.newAdminPassword = '';
        this.loadUsers();
      },
      error: err => this.notify.error(err?.error || 'Error al crear admin'),
    });
  }

  deleteAdmin(id: number) {
    if (!confirm('¿Eliminar este admin?')) return;
    this.userMgmt.deleteAdmin(id).subscribe({
      next: () => { this.notify.success('Admin eliminado'); this.loadUsers(); },
      error: () => this.notify.error('Error al eliminar admin'),
    });
  }

  deleteCliente(id: number) {
    if (!confirm('¿Eliminar este usuario?')) return;
    this.userMgmt.deleteCliente(id).subscribe({
      next: () => { this.notify.success('Usuario eliminado'); this.loadUsers(); },
      error: () => this.notify.error('Error al eliminar usuario'),
    });
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  get totalRevenue() {
    return this.orders.orders().reduce((s, o) => s + o.total, 0);
  }

  get totalOrders() {
    return this.orders.orders().length;
  }

  get totalProducts() {
    return this.products.PRODUCTS.length;
  }

  formatPrice(p: number) {
    return '$' + (p / 4000).toLocaleString('es-CO', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  formatDate(d: string) {
    const [year, month, day] = d.split('T')[0].split('-').map(Number);
    return new Date(year, month - 1, day).toLocaleDateString('es-CO', {
      month: 'short', day: 'numeric', year: 'numeric',
    });
  }

  logout() {
    this.auth.logout();
    this.orders.clearOrders();
    this.notify.info('Sesión cerrada.');
    this.router.navigate(['/login']);
  }
}
