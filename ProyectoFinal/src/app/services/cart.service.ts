import { Injectable, signal, computed } from '@angular/core';
import { CartItem, ProductItem } from '../models';

@Injectable({ providedIn: 'root' })
export class CartService {
  private _items    = signal<CartItem[]>([]);
  private _isOpen   = signal(false);

  readonly items    = this._items.asReadonly();
  readonly isOpen   = this._isOpen.asReadonly();
  readonly count    = computed(() => this._items().reduce((s,i) => s + i.qty, 0));
  readonly subtotal = computed(() => this._items().reduce((s,i) => s + i.product.price * i.qty, 0));
  readonly total    = computed(() => this.subtotal());

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

  clearCart()   { this._items.set([]); }
  openCart()    { this._isOpen.set(true); }
  closeCart()   { this._isOpen.set(false); }
  toggleCart()  { this._isOpen.update(v => !v); }
}
