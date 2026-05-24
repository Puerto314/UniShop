import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Cart } from './cart';
import { CartService } from '../services/cart.service';
import { OrderService } from '../services/order.service';
import { NotificationService } from '../services/notification.service';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { signal } from '@angular/core';
import { CartItem, ProductItem, Order } from '../models';

const mockProduct: ProductItem = {
  id: 1, name: 'iPhone 15', price: 3490000,
  image: '', category: 'smartphones', rating: 4.8, reviews: 100, stock: 5,
};

const mockItems: CartItem[] = [{ product: mockProduct, qty: 1 }];

const mockOrder: Order = {
  id: 'ORD-2025-1234', date: '2025-05-24', items: mockItems,
  total: 3490000, status: 'pending',
};

describe('Cart', () => {
  let component: Cart;
  let fixture: ComponentFixture<Cart>;
  let cartSpy: jasmine.SpyObj<CartService>;
  let orderSpy: jasmine.SpyObj<OrderService>;
  let notifySpy: jasmine.SpyObj<NotificationService>;

  beforeEach(async () => {
    cartSpy   = jasmine.createSpyObj('CartService',   ['items', 'total', 'clearCart', 'closeCart', 'isOpen', 'count', 'subtotal', 'remove', 'updateQty', 'toggleCart']);
    orderSpy  = jasmine.createSpyObj('OrderService',  ['placeOrder']);
    notifySpy = jasmine.createSpyObj('NotificationService', ['success', 'error', 'info']);

    // Signal-compatible stubs
    cartSpy.items.and.returnValue(mockItems);
    cartSpy.total.and.returnValue(3490000);
    cartSpy.isOpen.and.returnValue(false);
    cartSpy.count.and.returnValue(1);
    cartSpy.subtotal.and.returnValue(3490000);
    orderSpy.placeOrder.and.returnValue(mockOrder);

    await TestBed.configureTestingModule({
      imports: [Cart, NoopAnimationsModule],
      providers: [
        { provide: CartService,         useValue: cartSpy   },
        { provide: OrderService,        useValue: orderSpy  },
        { provide: NotificationService, useValue: notifySpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Cart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('checkout()', () => {
    it('should do nothing when cart is empty', () => {
      cartSpy.items.and.returnValue([]);
      component.checkout();
      expect(orderSpy.placeOrder).not.toHaveBeenCalled();
    });

    it('should set checkingOut to true while processing', fakeAsync(() => {
      component.checkout();
      expect(component.checkingOut()).toBeTrue();
      tick(1800);
    }));

    it('should call placeOrder after 1800ms', fakeAsync(() => {
      component.checkout();
      tick(1800);
      expect(orderSpy.placeOrder).toHaveBeenCalledWith(mockItems, 3490000);
    }));

    it('should clear and close cart after checkout', fakeAsync(() => {
      component.checkout();
      tick(1800);
      expect(cartSpy.clearCart).toHaveBeenCalled();
      expect(cartSpy.closeCart).toHaveBeenCalled();
    }));

    it('should show success notification with order id', fakeAsync(() => {
      component.checkout();
      tick(1800);
      expect(notifySpy.success).toHaveBeenCalledWith(jasmine.stringContaining('ORD-2025-1234'));
    }));

    it('should set checkingOut back to false after checkout', fakeAsync(() => {
      component.checkout();
      tick(1800);
      expect(component.checkingOut()).toBeFalse();
    }));
  });

  describe('formatPrice()', () => {
    it('should return "Ver precio" for 0', () => {
      expect(component.formatPrice(0)).toBe('Ver precio');
    });

    it('should return "Ver precio" for negative', () => {
      expect(component.formatPrice(-100)).toBe('Ver precio');
    });

    it('should divide by 4000 and format with $', () => {
      const result = component.formatPrice(4000);
      expect(result).toContain('$');
      expect(result).toContain('1');
    });
  });

  describe('priceLabel()', () => {
    it('should return "Ver precio en Amazon" for 0 price', () => {
      expect(component.priceLabel(0)).toBe('Ver precio en Amazon');
    });

    it('should return formatted price for positive value', () => {
      expect(component.priceLabel(4000000)).toContain('$');
    });
  });
});
