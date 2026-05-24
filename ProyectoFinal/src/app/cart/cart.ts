import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { trigger, transition, style, animate } from '@angular/animations';
import { CartService } from '../services/cart.service';
import { OrderService } from '../services/order.service';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.html',
  styleUrl: './cart.css',
  animations: [
    trigger('drawerAnim', [
      transition(':enter', [
        style({ transform: 'translateX(100%)' }),
        animate('350ms cubic-bezier(.4,0,.2,1)', style({ transform: 'translateX(0)' }))
      ]),
      transition(':leave', [animate('280ms ease', style({ transform: 'translateX(100%)' }))])
    ]),
    trigger('itemAnim', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(30px)' }),
        animate('250ms ease', style({ opacity: 1, transform: 'translateX(0)' }))
      ]),
      transition(':leave', [animate('200ms ease', style({ opacity: 0, transform: 'translateX(30px)' }))])
    ])
  ]
})
export class Cart {
  checkingOut = signal(false);

  constructor(
    public cartService: CartService,
    private orders: OrderService,
    private notify: NotificationService,
  ) {}

  checkout() {
    if (!this.cartService.items().length) return;
    this.checkingOut.set(true);
    setTimeout(() => {
      const order = this.orders.placeOrder(this.cartService.items(), this.cartService.total());
      this.cartService.clearCart();
      this.cartService.closeCart();
      this.checkingOut.set(false);
      this.notify.success(`Pedido ${order.id} realizado. ¡Gracias por tu compra!`);
    }, 1800);
  }

  /** Formatea un precio en COP */
  formatPrice(p: number) {
    if (!p || p <= 0) return 'Ver precio';

    const converted = p / 4000;

    return '$' + converted.toLocaleString('es-CO', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  /** Etiqueta del precio: si es producto Amazon, muestra COP con nota */
  priceLabel(price: number, category?: string): string {
    if (!price || price <= 0) return 'Ver precio en Amazon';
    return this.formatPrice(price);
  }
}
