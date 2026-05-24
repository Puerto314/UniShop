import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { OrderService } from './order.service';
import { AuthService } from './auth.service';
import { CartItem, ProductItem } from '../models';

const mockProduct: ProductItem = {
  id: 1, name: 'iPhone 15', price: 3490000,
  image: '', category: 'smartphones', rating: 4.8, reviews: 100, stock: 5,
};

const mockItems: CartItem[] = [
  { product: mockProduct, qty: 2 },
];

const mockBackendOrden = {
  id: 1,
  ordenId: 'ORD-2025-1234',
  fecha: '2025-05-24T00:00:00',
  total: 6980000,
  itemsJson: JSON.stringify(mockItems),
  status: 'pending',
};

describe('OrderService', () => {
  let service: OrderService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });
    service = TestBed.inject(OrderService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('initial state', () => {
    it('should start with empty orders', () => {
      expect(service.orders().length).toBe(0);
    });
  });

  describe('loadMisOrdenes()', () => {
    it('should GET /ordenes/mias and populate orders', () => {
      service.loadMisOrdenes().subscribe();
      const req = httpMock.expectOne('https://gpcueb.com/unishop/ordenes/mias');
      expect(req.request.method).toBe('GET');
      req.flush([mockBackendOrden]);

      expect(service.orders().length).toBe(1);
      expect(service.orders()[0].id).toBe('ORD-2025-1234');
    });

    it('should parse itemsJson correctly', () => {
      service.loadMisOrdenes().subscribe();
      httpMock.expectOne('https://gpcueb.com/unishop/ordenes/mias').flush([mockBackendOrden]);
      expect(service.orders()[0].items.length).toBe(1);
      expect(service.orders()[0].items[0].product.id).toBe(1);
    });

    it('should handle malformed itemsJson gracefully', () => {
      service.loadMisOrdenes().subscribe();
      httpMock.expectOne('https://gpcueb.com/unishop/ordenes/mias')
        .flush([{ ...mockBackendOrden, itemsJson: 'NOT_JSON' }]);
      expect(service.orders()[0].items).toEqual([]);
    });
  });

  describe('loadTodasLasOrdenes()', () => {
    it('should GET /ordenes/todas', () => {
      service.loadTodasLasOrdenes().subscribe();
      const req = httpMock.expectOne('https://gpcueb.com/unishop/ordenes/todas');
      expect(req.request.method).toBe('GET');
      req.flush([{ ...mockBackendOrden, clienteNombre: 'Juan' }]);
      expect(service.orders()[0].clienteNombre).toBe('Juan');
    });
  });

  describe('placeOrder()', () => {
    it('should add a new order to orders signal immediately', () => {
      service.placeOrder(mockItems, 6980000);
      expect(service.orders().length).toBe(1);
      // absorb the fire-and-forget POST
      httpMock.expectOne('https://gpcueb.com/unishop/ordenes').flush({});
    });

    it('should generate an order id starting with ORD-', () => {
      const order = service.placeOrder(mockItems, 6980000);
      expect(order.id).toMatch(/^ORD-\d{4}-\d+$/);
      httpMock.expectOne('https://gpcueb.com/unishop/ordenes').flush({});
    });

    it('should set status to pending', () => {
      const order = service.placeOrder(mockItems, 6980000);
      expect(order.status).toBe('pending');
      httpMock.expectOne('https://gpcueb.com/unishop/ordenes').flush({});
    });

    it('should include correct total', () => {
      const order = service.placeOrder(mockItems, 9999);
      expect(order.total).toBe(9999);
      httpMock.expectOne('https://gpcueb.com/unishop/ordenes').flush({});
    });

    it('should POST order payload to backend', () => {
      service.placeOrder(mockItems, 6980000);
      const req = httpMock.expectOne('https://gpcueb.com/unishop/ordenes');
      expect(req.request.method).toBe('POST');
      expect(req.request.body.total).toBe(6980000);
    });
  });

  describe('clearOrders()', () => {
    it('should empty the orders signal', () => {
      service.placeOrder(mockItems, 100);
      httpMock.expectOne('https://gpcueb.com/unishop/ordenes').flush({});
      service.clearOrders();
      expect(service.orders().length).toBe(0);
    });
  });

  describe('statusLabel()', () => {
    it('should return correct label for each status', () => {
      expect(service.statusLabel('pending')).toBe('Pendiente');
      expect(service.statusLabel('processing')).toBe('Procesando');
      expect(service.statusLabel('shipped')).toBe('Enviado');
      expect(service.statusLabel('delivered')).toBe('Entregado');
      expect(service.statusLabel('cancelled')).toBe('Cancelado');
    });
  });

  describe('statusColor()', () => {
    it('should return a CSS variable string for each status', () => {
      expect(service.statusColor('pending')).toContain('var(');
      expect(service.statusColor('delivered')).toContain('var(');
    });
  });
});
