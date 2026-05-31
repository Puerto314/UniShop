import {Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Order, CartItem} from '../models';
import {AuthService} from './auth.service';

const API_URL = 'https://gpcueb.org/unishop';

/** Shape que devuelve el backend */
interface OrdenBackend {
  id: number;
  ordenId: string;
  fecha: string;
  total: number;
  itemsJson: string;
  status: string;
  clienteNombre?: string; // solo en /todas (admin)
}

@Injectable({providedIn: 'root'})
export class OrderService {

  private _orders = signal<Order[]>([]);
  readonly orders = this._orders.asReadonly();

  constructor(private http: HttpClient, private auth: AuthService) {
  }

  // ── Carga el historial desde el backend ─────────────────────────────────

  /** Llama a /ordenes/mias (usuario autenticado) */
  loadMisOrdenes(): Observable<OrdenBackend[]> {
    return this.http.get<OrdenBackend[]>(`${API_URL}/ordenes/mias`).pipe(
      tap(data => this._orders.set(data.map(this.mapBackendToOrder)))
    );
  }

  /** Llama a /ordenes/todas (solo admin) */
  loadTodasLasOrdenes(): Observable<OrdenBackend[]> {
    return this.http.get<OrdenBackend[]>(`${API_URL}/ordenes/todas`).pipe(
      tap(data => this._orders.set(data.map(this.mapBackendToOrder)))
    );
  }

  // ── Guarda un pedido en el backend y actualiza el signal ────────────────

  placeOrder(items: CartItem[], total: number): Order {
    // Generar el ID legible en el cliente (el backend usa su propio PK)
    const ordenId = `ORD-${new Date().getFullYear()}-${String(Date.now()).slice(-4)}`;
    const now = new Date();
    const dateStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;

    const order: Order = {
      id: ordenId,
      date: dateStr,
      items: [...items],
      total,
      status: 'pending',
    };

    const payload = {
      ordenId,
      fecha: dateStr,
      total,
      itemsJson: JSON.stringify(items),
      status: 'pending',
    };

    // Llamada al backend (fire-and-forget: el interceptor ya pone el token)
    this.http.post(`${API_URL}/ordenes`, payload).subscribe({
      error: err => console.error('[OrderService] Error guardando orden:', err),
    });

    // Actualizar la señal local inmediatamente (UX optimista)
    this._orders.update(prev => [order, ...prev]);
    return order;
  }

  // ── Limpia el historial local (al hacer logout) ──────────────────────────

  clearOrders(): void {
    this._orders.set([]);
  }

  // ── Helpers ─────────────────────────────────────────────────────────────

  private mapBackendToOrder(o: OrdenBackend): Order {
    let items: CartItem[] = [];
    try {
      items = JSON.parse(o.itemsJson ?? '[]');
    } catch {
      items = [];
    }
    return {
      id: o.ordenId,
      date: o.fecha,
      items,
      total: Number(o.total),
      status: (o.status as Order['status']) ?? 'pending',
      clienteNombre: o.clienteNombre,
    };
  }

  statusColor(s: Order['status']) {
    return {
      pending: 'var(--yellow)',
      processing: 'var(--cyan)',
      shipped: 'var(--magenta)',
      delivered: 'var(--green)',
      cancelled: 'var(--red)',
    }[s];
  }

  statusLabel(s: Order['status']) {
    return {
      pending: 'Pendiente',
      processing: 'Procesando',
      shipped: 'Enviado',
      delivered: 'Entregado',
      cancelled: 'Cancelado',
    }[s];
  }
}
