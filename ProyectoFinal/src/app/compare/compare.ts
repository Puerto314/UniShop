import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CompareService } from '../services/compare.service';
import { AmazonProduct } from '../models';

@Component({
  selector: 'app-compare',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './compare.html',
  styleUrl: './compare.css',
})
export class Compare {

  searchQuery = '';

  constructor(public svc: CompareService) {}

  search(): void {
    if (this.searchQuery.trim()) {
      this.svc.buscar(this.searchQuery);
    }
  }

  onKey(event: KeyboardEvent): void {
    if (event.key === 'Enter') this.search();
  }

  openProduct(product: AmazonProduct): void {
    if (product.url) window.open(product.url, '_blank');
  }

  verResenas(product: AmazonProduct): void {
    // Toggle: si ya están abiertas para este producto, cerrar
    if (this.svc.reviewsFor()?.asin === product.asin) {
      this.svc.cerrarResenas();
    } else {
      this.svc.cargarResenas(product);
    }
  }

  cerrarResenas(): void {
    this.svc.cerrarResenas();
  }
}
