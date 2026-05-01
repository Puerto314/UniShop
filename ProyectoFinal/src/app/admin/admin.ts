import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../services/product.service';
import { OrderService } from '../services/order.service';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin {
  activeTab = signal<'dashboard'|'products'|'orders'>('dashboard');

  constructor(
    public products: ProductService,
    public orders: OrderService,
    public auth: AuthService,
    private notify: NotificationService,
  ) {}

  get totalRevenue() {
    return this.orders.orders().reduce((s,o) => s + o.total, 0);
  }
  get totalOrders()   { return this.orders.orders().length; }
  get totalProducts() { return this.products.PRODUCTS.length; }
  get avgRating()     { return (this.products.PRODUCTS.reduce((s,p)=>s+p.rating,0)/this.products.PRODUCTS.length).toFixed(1); }

  formatPrice(p: number) { return '$' + p.toLocaleString('es-CO'); }
  formatDate(d: string)  { return new Date(d).toLocaleDateString('es-CO', { month:'short', day:'numeric' }); }

  changeStatus(orderId: string) {
    this.notify.info(`Estado del pedido ${orderId} actualizado`);
  }
}
