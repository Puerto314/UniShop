import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductItem } from '../../models';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css',
})
export class ProductCard {
  @Input() product!: ProductItem;
  @Output() addToCart   = new EventEmitter<ProductItem>();
  @Output() quickView   = new EventEmitter<ProductItem>();
  @Output() addWishlist = new EventEmitter<ProductItem>();

  added = false;
  wishlisted = false;

  onAdd() {
    this.addToCart.emit(this.product);
    this.added = true;
    setTimeout(() => this.added = false, 1800);
  }

  onWishlist() {
    this.wishlisted = !this.wishlisted;
    this.addWishlist.emit(this.product);
  }

  get discount() {
    if (!this.product.originalPrice) return 0;
    return Math.round((1 - this.product.price / this.product.originalPrice) * 100);
  }

  get stars() {
    return Array.from({length: 5}, (_, i) => i < Math.round(this.product.rating) ? '★' : '☆');
  }

  formatPrice(p: number) {
    return '$' + p.toLocaleString('es-CO');
  }
}
