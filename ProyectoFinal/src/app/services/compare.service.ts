import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { forkJoin, of, Observable } from 'rxjs';
import { switchMap, map, catchError } from 'rxjs/operators';
import {
  BuscarResponse, ExternalProduct,
  MercadoLibreSearchResponse, MercadoLibreRawItem
} from '../models';

@Injectable({ providedIn: 'root' })
export class CompareService {

  private readonly API = 'http://localhost:8080';

  readonly loading  = signal(false);
  readonly error    = signal('');
  readonly mlItems  = signal<ExternalProduct[]>([]);
  readonly amItems  = signal<ExternalProduct[]>([]);
  readonly searched = signal('');

  constructor(private http: HttpClient) {}

  buscar(nombre: string): void {
    if (!nombre.trim()) return;
    this.loading.set(true);
    this.error.set('');
    this.mlItems.set([]);
    this.amItems.set([]);
    this.searched.set(nombre.trim());

    this.http.get<BuscarResponse>(`${this.API}/producto/buscar?nombre=${encodeURIComponent(nombre)}`)
      .pipe(
        switchMap((resp: BuscarResponse) => {
          // Amazon ya viene del backend
          const amazon$ = of(resp.amazon);

          // MercadoLibre: el browser lo llama directamente
          const ml$: Observable<ExternalProduct[]> = resp.mercadoLibreUrl
            ? this.http.get<MercadoLibreSearchResponse>(resp.mercadoLibreUrl).pipe(
                map(mlResp => (mlResp.results || []).map((item: MercadoLibreRawItem): ExternalProduct => ({
                  nombreProducto:      item.title,
                  descripcionProducto: item.permalink,
                  precioProducto:      item.price,
                  tienda:              'MercadoLibre',
                  thumbnail:           item.thumbnail?.replace('http://', 'https://'),
                  permalink:           item.permalink,
                }))),
                catchError(() => of([]))
              )
            : of([]);

          return forkJoin({ amazon: amazon$, ml: ml$ });
        }),
        catchError(err => {
          this.error.set('Error al conectar con el servidor. Verifica que el backend esté corriendo en localhost:8080.');
          console.error(err);
          return of({ amazon: [], ml: [] });
        })
      )
      .subscribe(({ amazon, ml }) => {
        this.amItems.set(amazon);
        this.mlItems.set(ml);
        this.loading.set(false);
      });
  }

  formatPrice(price: number): string {
    return '$' + Math.round(price).toLocaleString('es-CO');
  }
}
