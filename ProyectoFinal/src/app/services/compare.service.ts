import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { AmazonProduct, AmazonReview, BuscarResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class CompareService {

  private readonly API = 'https://gpcueb.org/unishop';

  readonly loading        = signal(false);
  readonly loadingReviews = signal(false);
  readonly error          = signal('');
  readonly errorReviews   = signal('');
  readonly amItems        = signal<AmazonProduct[]>([]);
  readonly reviews        = signal<AmazonReview[]>([]);
  readonly searched       = signal('');
  readonly reviewsFor     = signal<AmazonProduct | null>(null);

  constructor(private http: HttpClient) {}

  /** Busca productos en Amazon y actualiza amItems. */
  buscar(nombre: string): void {
    if (!nombre.trim()) return;
    this.loading.set(true);
    this.error.set('');
    this.amItems.set([]);
    this.reviews.set([]);
    this.reviewsFor.set(null);
    this.searched.set(nombre.trim());

    this.http.get<BuscarResponse>(
      `${this.API}/producto/buscar?nombre=${encodeURIComponent(nombre)}`
    ).pipe(
      catchError(err => {
        this.error.set(
          'Error al conectar con el servidor. Verifica que el backend esté corriendo en gpcueb.org/unishop.'
        );
        console.error(err);
        return of({ amazon: [] } as BuscarResponse);
      })
    ).subscribe(resp => {
      this.amItems.set(resp.amazon ?? []);
      this.loading.set(false);
    });
  }

  /** Carga las reseñas de un producto dado su ASIN. */
  cargarResenas(product: AmazonProduct): void {
    this.loadingReviews.set(true);
    this.errorReviews.set('');
    this.reviews.set([]);
    this.reviewsFor.set(product);

    this.http.get<AmazonReview[]>(
      `${this.API}/producto/resenas?asin=${encodeURIComponent(product.asin)}`
    ).pipe(
      catchError(err => {
        this.errorReviews.set('No se pudieron cargar las reseñas.');
        console.error(err);
        return of([] as AmazonReview[]);
      })
    ).subscribe(data => {
      this.reviews.set(data ?? []);
      this.loadingReviews.set(false);
    });
  }

  /** Cierra el panel de reseñas. */
  cerrarResenas(): void {
    this.reviews.set([]);
    this.reviewsFor.set(null);
    this.errorReviews.set('');
  }

  /** El producto con el precio más bajo (con precio > 0). */
  get cheapest(): AmazonProduct | null {
    const lista = this.amItems().filter(p => p.price > 0);
    if (!lista.length) return null;
    return lista.reduce((a, b) => a.price < b.price ? a : b);
  }

  isCheapest(product: AmazonProduct): boolean {
    const c = this.cheapest;
    return !!c && c.asin === product.asin;
  }

  /** Formatea un número como precio en USD. */
  formatUSD(price: number): string {
    if (!price || price === 0) return 'Ver precio';
    return '$' + price.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  }

  /** Estrellas llenas (★) para un rating 0–5. */
  stars(rating: number | undefined): string {
    if (!rating) return '☆☆☆☆☆';
    const full  = Math.round(rating);
    const empty = 5 - full;
    return '★'.repeat(full) + '☆'.repeat(empty);
  }
}
