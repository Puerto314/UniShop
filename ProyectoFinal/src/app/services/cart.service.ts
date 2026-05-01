import { Injectable, signal, computed } from '@angular/core';
import { CartItem, ProductItem, CouponResult } from '../models';

@Injectable({ providedIn: 'root' })
export class CartService {
  private _items    = signal<CartItem[]>([]);
  private _discount = signal(0);
  private _isOpen   = signal(false);

  readonly items    = this._items.asReadonly();
  readonly isOpen   = this._isOpen.asReadonly();
  readonly count    = computed(() => this._items().reduce((s,i) => s + i.qty, 0));
  readonly subtotal = computed(() => this._items().reduce((s,i) => s + i.product.price * i.qty, 0));
  readonly discount = this._discount.asReadonly();
  readonly total    = computed(() => Math.round(this.subtotal() * (1 - this._discount())));

  private readonly COUPONS: Record<string, number> = { 'NEON10': .10, 'GAMER20': .20, 'VIP30': .30 };

  addToCart(product: ProductItem, qty = 1) {
    this._items.update(items => {
      const idx = items.findIndex(i => i.product.id === product.id);
      if (idx >= 0) return items.map((i,n) => n === idx ? { ...i, qty: i.qty + qty } : i);
      return [...items, { product, qty }];
    });
  }

  remove(productId: number)          { this._items.update(items => items.filter(i => i.product.id !== productId)); }
  updateQty(productId: number, qty: number) {
    if (qty < 1) { this.remove(productId); return; }
    this._items.update(items => items.map(i => i.product.id === productId ? { ...i, qty } : i));
  }

  applyCoupon(code: string): CouponResult {
    const d = this.COUPONS[code.toUpperCase()];
    if (d) { this._discount.set(d); return { success: true, message: `¡${d*100}% de descuento aplicado!`, discount: d }; }
    return { success: false, message: 'Cupón no válido' };
  }

  clearCart()   { this._items.set([]); this._discount.set(0); }
  openCart()    { this._isOpen.set(true); }
  closeCart()   { this._isOpen.set(false); }
  toggleCart()  { this._isOpen.update(v => !v); }
}
