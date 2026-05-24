import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CompareService } from '../services/compare.service';
import { CartService } from '../services/cart.service';
import { NotificationService } from '../services/notification.service';
import { AmazonProduct, ProductItem } from '../models';

@Component({
  selector: 'app-compare',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './compare.html',
  styleUrl: './compare.css',
})
export class Compare {
  searchQuery = '';

  constructor(
    public svc: CompareService,
    private cart: CartService,
    private notify: NotificationService,
  ) {}

  search(): void {
    if (this.searchQuery.trim()) this.svc.buscar(this.searchQuery);
  }

  quickSearch(term: string): void {
    this.searchQuery = term;
    this.svc.buscar(term);
  }

  onKey(event: KeyboardEvent): void {
    if (event.key === 'Enter') this.search();
  }

  openProduct(product: AmazonProduct): void {
    if (product.url) window.open(product.url, '_blank');
  }

  verResenas(product: AmazonProduct): void {
    if (this.svc.reviewsFor()?.asin === product.asin) {
      this.svc.cerrarResenas();
    } else {
      this.svc.cargarResenas(product);
    }
  }

  cerrarResenas(): void { this.svc.cerrarResenas(); }

  addToCart(product: AmazonProduct): void {
    // El precio de Amazon llega en USD desde el backend.
    // Se conserva el precio USD original y se convierte a COP para mostrar en el carrito.
    if (!product.price || product.price <= 0) {
      this.notify.info('Precio no disponible — visita Amazon para ver el precio actual.');
      return;
    }

    const priceCOP = Math.round(product.price * 4000); // USD → COP aproximado

    const item: ProductItem = {
      id: product.asin.split('').reduce((a, c) => a + c.charCodeAt(0), 0),
      name: product.title.length > 80 ? product.title.substring(0, 80) + '...' : product.title,
      price: priceCOP,          // precio en COP para mostrar en carrito y factura
      originalPrice: product.price, // precio original en USD (referencia)
      image: product.imageUrl || '',
      category: 'amazon',
      rating: product.rating ?? 4.5,
      reviews: product.reviewCount ?? 0,
      stock: 10,
      amazonUrl: product.url,
      asin: product.asin,
    };

    this.cart.addToCart(item);
    this.cart.openCart();
    this.notify.success(`${item.name.substring(0, 40)}... agregado al carrito`);
  }

  /** Muestra el precio en USD con formato legible */
  formatUSD(price: number): string {
    return this.svc.formatUSD(price);
  }

  /** Muestra el precio COP equivalente */
  formatCOP(priceUSD: number): string {
    if (!priceUSD || priceUSD <= 0) return 'Ver precio';
    return '$' + Math.round(priceUSD * 4000).toLocaleString('es-CO');
  }
}
