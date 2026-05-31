import { ComponentFixture, TestBed } from '@angular/core/testing';
import { History } from './history';
import { OrderService } from '../services/order.service';
import { CartService } from '../services/cart.service';
import { NotificationService } from '../services/notification.service';
import { signal } from '@angular/core';
import { of, throwError } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { Order } from '../models';

const mockOrder: Order = {
  id: 'ORD-2025-0001',
  date: '2025-05-24T00:00:00',
  items: [
    { product: { id: 1, name: 'iPhone 15', price: 3490000, image: '', category: 'smartphones', rating: 4.8, reviews: 100, stock: 5 }, qty: 1 },
  ],
  total: 3490000,
  status: 'delivered',
};

describe('History', () => {
  let component: History;
  let fixture: ComponentFixture<History>;
  let orderSpy: jasmine.SpyObj<OrderService>;
  let cartSpy: jasmine.SpyObj<CartService>;
  let notifySpy: jasmine.SpyObj<NotificationService>;

  beforeEach(async () => {
    orderSpy  = jasmine.createSpyObj('OrderService',  ['loadMisOrdenes', 'orders']);
    cartSpy   = jasmine.createSpyObj('CartService',   ['addToCart', 'openCart']);
    notifySpy = jasmine.createSpyObj('NotificationService', ['success', 'error']);

    // orders() debe ser un signal-like para que el template no explote
    orderSpy.orders.and.returnValue([mockOrder]);
    orderSpy.loadMisOrdenes.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [History, RouterTestingModule],
      providers: [
        { provide: OrderService,        useValue: orderSpy  },
        { provide: CartService,         useValue: cartSpy   },
        { provide: NotificationService, useValue: notifySpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(History);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit()', () => {
    it('should call loadMisOrdenes on init', () => {
      expect(orderSpy.loadMisOrdenes).toHaveBeenCalled();
    });

    it('should set loading to false on success', () => {
      expect(component.loading).toBeFalse();
    });

    it('should set loading false and show error on failure', () => {
      orderSpy.loadMisOrdenes.and.returnValue(throwError(() => new Error('fail')));
      component.ngOnInit();
      expect(component.loading).toBeFalse();
      expect(notifySpy.error).toHaveBeenCalled();
    });
  });

  describe('reorder()', () => {
    it('should add all order items to cart', () => {
      component.reorder('ORD-2025-0001');
      expect(cartSpy.addToCart).toHaveBeenCalledWith(mockOrder.items[0].product, 1);
    });

    it('should open cart after reorder', () => {
      component.reorder('ORD-2025-0001');
      expect(cartSpy.openCart).toHaveBeenCalled();
    });

    it('should show success notification', () => {
      component.reorder('ORD-2025-0001');
      expect(notifySpy.success).toHaveBeenCalled();
    });

    it('should do nothing for unknown order id', () => {
      component.reorder('NON_EXISTENT');
      expect(cartSpy.addToCart).not.toHaveBeenCalled();
    });
  });

  describe('verFactura() / cerrarFactura()', () => {
    it('should set facturaOrder', () => {
      component.verFactura(mockOrder);
      expect(component.facturaOrder).toBe(mockOrder);
    });

    it('should clear facturaOrder on cerrarFactura', () => {
      component.verFactura(mockOrder);
      component.cerrarFactura();
      expect(component.facturaOrder).toBeNull();
    });
  });

  describe('formatPrice()', () => {
    it('should return "Ver precio" for 0', () => {
      expect(component.formatPrice(0)).toBe('Ver precio');
    });

    it('should return "Ver precio" for negative', () => {
      expect(component.formatPrice(-1)).toBe('Ver precio');
    });

    it('should format valid price with $', () => {
      expect(component.formatPrice(4000000)).toContain('$');
    });
  });

  describe('formatDate()', () => {
    it('should return empty string for empty input', () => {
      expect(component.formatDate('')).toBe('');
    });

    it('should format a valid date string', () => {
      const result = component.formatDate('2025-05-24T00:00:00');
      expect(result).toContain('2025');
    });
  });
});
