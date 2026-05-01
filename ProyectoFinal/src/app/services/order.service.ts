import { Injectable, signal } from '@angular/core';
import { Order, CartItem } from '../models';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private _orders = signal<Order[]>([
    { id:'ORD-2025-001', date:'2025-11-10', total:378000, status:'delivered',
      items:[{ product:{ id:1,name:'Teclado Mecánico RGB K95',price:189000,image:'',category:'gaming',rating:4.9,reviews:312,stock:12 }, qty:2 }] },
    { id:'ORD-2025-002', date:'2025-12-03', total:129000, status:'shipped',
      items:[{ product:{ id:2,name:'Mouse Inalámbrico Pro X',price:129000,image:'',category:'gaming',rating:4.7,reviews:198,stock:25 }, qty:1 }] },
    { id:'ORD-2026-001', date:'2026-01-18', total:249000, status:'processing',
      items:[{ product:{ id:3,name:'Audífonos Gamer 7.1',price:249000,image:'',category:'audio',rating:4.8,reviews:445,stock:8 }, qty:1 }] },
  ]);

  readonly orders = this._orders.asReadonly();

  placeOrder(items: CartItem[], total: number) {
    const id = `ORD-${new Date().getFullYear()}-${String(this._orders().length + 1).padStart(3,'0')}`;
    const order: Order = { id, date: new Date().toISOString().split('T')[0], items: [...items], total, status: 'pending' };
    this._orders.update(o => [order, ...o]);
    return order;
  }

  statusColor(s: Order['status']) {
    return { pending:'var(--yellow)', processing:'var(--cyan)', shipped:'var(--magenta)', delivered:'var(--green)', cancelled:'var(--red)' }[s];
  }
  statusLabel(s: Order['status']) {
    return { pending:'Pendiente', processing:'Procesando', shipped:'Enviado', delivered:'Entregado', cancelled:'Cancelado' }[s];
  }
}
