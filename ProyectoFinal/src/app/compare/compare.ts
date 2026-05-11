import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CompareService } from '../services/compare.service';
import { ExternalProduct } from '../models';

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

  search() {
    if (this.searchQuery.trim()) {
      this.svc.buscar(this.searchQuery);
    }
  }

  onKey(event: KeyboardEvent) {
    if (event.key === 'Enter') this.search();
  }

  openLink(product: ExternalProduct) {
    const url = product.permalink || product.descripcionProducto;
    if (url && url.startsWith('http')) window.open(url, '_blank');
  }

  get allResults(): ExternalProduct[] {
    return [...this.svc.mlItems(), ...this.svc.amItems()];
  }

  get cheapestML(): ExternalProduct | null {
    const list = this.svc.mlItems().filter(p => p.precioProducto > 0);
    if (!list.length) return null;
    return list.reduce((a, b) => a.precioProducto < b.precioProducto ? a : b);
  }

  get cheapestAM(): ExternalProduct | null {
    const list = this.svc.amItems().filter(p => p.precioProducto > 0);
    if (!list.length) return null;
    return list.reduce((a, b) => a.precioProducto < b.precioProducto ? a : b);
  }

  get overallCheapest(): ExternalProduct | null {
    const candidates = [this.cheapestML, this.cheapestAM].filter(Boolean) as ExternalProduct[];
    if (!candidates.length) return null;
    return candidates.reduce((a, b) => a.precioProducto < b.precioProducto ? a : b);
  }

  isCheapest(product: ExternalProduct): boolean {
    const cheapest = this.overallCheapest;
    return !!cheapest &&
      cheapest.nombreProducto === product.nombreProducto &&
      cheapest.precioProducto === product.precioProducto;
  }
}
