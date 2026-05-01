import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { OrderService } from '../services/order.service';
import { CartService } from '../services/cart.service';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './history.html',
  styleUrl: './history.css',
})
export class History {
  constructor(
    public orderService: OrderService,
    public cart: CartService,
    private notify: NotificationService,
  ) {}

  reorder(orderId: string) {
    const order = this.orderService.orders().find(o => o.id === orderId);
    if (!order) return;
    order.items.forEach(item => this.cart.addToCart(item.product, item.qty));
    this.cart.openCart();
    this.notify.success('Productos del pedido agregados al carrito');
  }

  formatPrice(p: number) { return '$' + p.toLocaleString('es-CO'); }
  formatDate(d: string)  { return new Date(d).toLocaleDateString('es-CO', { year:'numeric', month:'long', day:'numeric' }); }
}
