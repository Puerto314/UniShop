import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {OrderService} from '../services/order.service';
import {CartService} from '../services/cart.service';
import {NotificationService} from '../services/notification.service';
import {Order} from '../models';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './history.html',
  styleUrl: './history.css',
})
export class History {
  facturaOrder: Order | null = null;
  readonly currentYear = new Date().getFullYear();

  constructor(
    public orderService: OrderService,
    public cart: CartService,
    private notify: NotificationService,
  ) {
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
      maximumFractionDigits: 2
    });
  }

  /**
   * Parsea la fecha evitando el desfase de zona horaria.
   * new Date("2025-11-10") se interpreta como UTC medianoche, lo que en zonas
   * UTC-5 (Colombia) da el día anterior. Se parsea manualmente como fecha local.
   */
  formatDate(d: string): string {
    if (!d) return '';
    const dateStr = d.split('T')[0]; // soporta "2025-11-10" y "2025-11-10T..."
    const [year, month, day] = dateStr.split('-').map(Number);
    return new Date(year, month - 1, day).toLocaleDateString('es-CO', {
      year: 'numeric', month: 'long', day: 'numeric'
    });
  }
}
