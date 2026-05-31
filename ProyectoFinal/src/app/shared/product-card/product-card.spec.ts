import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProductCard } from './product-card';
import { ProductItem } from '../../models';

const mockProduct: ProductItem = {
  id: 1,
  name: 'iPhone 15',
  price: 3490000,
  originalPrice: 3890000,
  image: 'https://img.jpg',
  category: 'smartphones',
  rating: 4,
  reviews: 1000,
  stock: 10,
  tags: ['USB-C', '48MP'],
};

describe('ProductCard', () => {
  let component: ProductCard;
  let fixture: ComponentFixture<ProductCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductCard],
    }).compileComponents();

    fixture = TestBed.createComponent(ProductCard);
    component = fixture.componentInstance;
    component.product = mockProduct;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('discount getter', () => {
    it('should calculate discount percentage correctly', () => {
      // (1 - 3490000/3890000) * 100 ≈ 10
      expect(component.discount).toBe(Math.round((1 - 3490000 / 3890000) * 100));
    });

    it('should return 0 when originalPrice is not set', () => {
      component.product = { ...mockProduct, originalPrice: undefined };
      expect(component.discount).toBe(0);
    });
  });

  describe('stars getter', () => {
    it('should return an array of 5 elements', () => {
      expect(component.stars.length).toBe(5);
    });

    it('should have filled stars equal to rounded rating', () => {
      component.product = { ...mockProduct, rating: 4 };
      const filled = component.stars.filter(s => s === '★').length;
      expect(filled).toBe(4);
    });

    it('should have correct empty stars', () => {
      component.product = { ...mockProduct, rating: 3 };
      const empty = component.stars.filter(s => s === '☆').length;
      expect(empty).toBe(2);
    });
  });

  describe('formatPrice()', () => {
    it('should format price with $ prefix', () => {
      expect(component.formatPrice(3490000)).toContain('$');
    });

    it('should format using es-CO locale (dots as thousands separator)', () => {
      const result = component.formatPrice(3490000);
      expect(result).toContain('3');
    });
  });

  describe('onAdd()', () => {
    it('should emit addToCart with the product', () => {
      spyOn(component.addToCart, 'emit');
      component.onAdd();
      expect(component.addToCart.emit).toHaveBeenCalledWith(mockProduct);
    });

    it('should set added to true after click', () => {
      component.onAdd();
      expect(component.added).toBeTrue();
    });

    it('should reset added to false after 1800ms', fakeAsync(() => {
      component.onAdd();
      expect(component.added).toBeTrue();
      tick(1800);
      expect(component.added).toBeFalse();
    }));
  });

  describe('onWishlist()', () => {
    it('should emit addWishlist with the product', () => {
      spyOn(component.addWishlist, 'emit');
      component.onWishlist();
      expect(component.addWishlist.emit).toHaveBeenCalledWith(mockProduct);
    });

    it('should toggle wishlisted', () => {
      expect(component.wishlisted).toBeFalse();
      component.onWishlist();
      expect(component.wishlisted).toBeTrue();
      component.onWishlist();
      expect(component.wishlisted).toBeFalse();
    });
  });
});
