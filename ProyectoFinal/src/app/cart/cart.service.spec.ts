import { TestBed } from '@angular/core/testing';
import { CartService } from './cart.service';
import { ProductItem } from '../models';

const mockProduct: ProductItem = {
  id: 1,
  name: 'iPhone 15',
  price: 3490000,
  originalPrice: 3890000,
  image: 'img.jpg',
  category: 'smartphones',
  rating: 4.8,
  reviews: 100,
  stock: 10,
};

const mockProduct2: ProductItem = {
  id: 2,
  name: 'Samsung S24',
  price: 4290000,
  image: 'img2.jpg',
  category: 'smartphones',
  rating: 4.7,
  reviews: 80,
  stock: 5,
};

describe('CartService', () => {
  let service: CartService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('addToCart', () => {
    it('should add a new product to the cart', () => {
      service.addToCart(mockProduct);
      expect(service.items().length).toBe(1);
      expect(service.items()[0].product.id).toBe(1);
      expect(service.items()[0].qty).toBe(1);
    });

    it('should increase qty when adding an existing product', () => {
      service.addToCart(mockProduct);
      service.addToCart(mockProduct, 2);
      expect(service.items().length).toBe(1);
      expect(service.items()[0].qty).toBe(3);
    });

    it('should add multiple different products', () => {
      service.addToCart(mockProduct);
      service.addToCart(mockProduct2);
      expect(service.items().length).toBe(2);
    });
  });

  describe('remove', () => {
    it('should remove a product by id', () => {
      service.addToCart(mockProduct);
      service.addToCart(mockProduct2);
      service.remove(1);
      expect(service.items().length).toBe(1);
      expect(service.items()[0].product.id).toBe(2);
    });

    it('should do nothing if product id does not exist', () => {
      service.addToCart(mockProduct);
      service.remove(999);
      expect(service.items().length).toBe(1);
    });
  });

  describe('updateQty', () => {
    it('should update quantity of a product', () => {
      service.addToCart(mockProduct);
      service.updateQty(1, 5);
      expect(service.items()[0].qty).toBe(5);
    });

    it('should remove product if qty is set to 0', () => {
      service.addToCart(mockProduct);
      service.updateQty(1, 0);
      expect(service.items().length).toBe(0);
    });

    it('should remove product if qty is negative', () => {
      service.addToCart(mockProduct);
      service.updateQty(1, -1);
      expect(service.items().length).toBe(0);
    });
  });

  describe('clearCart', () => {
    it('should empty all items', () => {
      service.addToCart(mockProduct);
      service.addToCart(mockProduct2);
      service.clearCart();
      expect(service.items().length).toBe(0);
    });
  });

  describe('count', () => {
    it('should return total item count', () => {
      service.addToCart(mockProduct, 3);
      service.addToCart(mockProduct2, 2);
      expect(service.count()).toBe(5);
    });

    it('should return 0 when cart is empty', () => {
      expect(service.count()).toBe(0);
    });
  });

  describe('subtotal / total', () => {
    it('should calculate subtotal correctly', () => {
      service.addToCart(mockProduct, 2); // 3490000 * 2 = 6980000
      expect(service.subtotal()).toBe(6980000);
    });

    it('should match total with subtotal', () => {
      service.addToCart(mockProduct, 1);
      expect(service.total()).toBe(service.subtotal());
    });
  });

  describe('cart drawer', () => {
    it('should start closed', () => {
      expect(service.isOpen()).toBeFalse();
    });

    it('should open cart', () => {
      service.openCart();
      expect(service.isOpen()).toBeTrue();
    });

    it('should close cart', () => {
      service.openCart();
      service.closeCart();
      expect(service.isOpen()).toBeFalse();
    });

    it('should toggle cart open and closed', () => {
      service.toggleCart();
      expect(service.isOpen()).toBeTrue();
      service.toggleCart();
      expect(service.isOpen()).toBeFalse();
    });
  });
});
