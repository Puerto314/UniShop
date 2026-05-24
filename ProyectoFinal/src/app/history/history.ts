import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { OrderService } from '../services/order.service';
import { CartService } from '../services/cart.service';
import { NotificationService } from '../services/notification.service';
import { Order } from '../models';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './history.html',
  styleUrl: './history.css',
})
export class History implements OnInit {
  facturaOrder: Order | null = null;
  readonly currentYear = new Date().getFullYear();
  loading = false;

  constructor(
    public orderService: OrderService,
    public cart: CartService,
    private notify: NotificationService,
  ) {}

  ngOnInit(): void {
    this.loading = true;
    this.orderService.loadMisOrdenes().subscribe({
      next: () => (this.loading = false),
      error: () => {
        this.notify.error('No se pudo cargar el historial de pedidos');
        this.loading = false;
      },
    });
  }

  reorder(orderId: string) {
    const order = this.orderService.orders().find(o => o.id === orderId);
    if (!order) return;
    order.items.forEach(item => this.cart.addToCart(item.product, item.qty));
    this.cart.openCart();
    this.notify.success('Productos del pedido agregados al carrito');
  }

  verFactura(order: Order) {
    this.facturaOrder = order;
  }

  cerrarFactura() {
    this.facturaOrder = null;
  }

  formatPrice(p: number) {
    if (!p || p <= 0) return 'Ver precio';
    const converted = p / 4000;
    return '$' + converted.toLocaleString('es-CO', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });
  }

  formatDate(d: string): string {
    if (!d) return '';
    const dateStr = d.split('T')[0];
    const [year, month, day] = dateStr.split('-').map(Number);
    return new Date(year, month - 1, day).toLocaleDateString('es-CO', {
      year: 'numeric', month: 'long', day: 'numeric',
    });
  }
}
