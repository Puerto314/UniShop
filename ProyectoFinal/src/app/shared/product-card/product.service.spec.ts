import { TestBed } from '@angular/core/testing';
import { ProductService } from './product.service';

describe('ProductService', () => {
  let service: ProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('PRODUCTS catalog', () => {
    it('should have 12 products', () => {
      expect(service.PRODUCTS.length).toBe(12);
    });

    it('every product should have required fields', () => {
      service.PRODUCTS.forEach(p => {
        expect(p.id).toBeDefined();
        expect(p.name).toBeTruthy();
        expect(p.price).toBeGreaterThan(0);
        expect(p.category).toBeTruthy();
        expect(p.rating).toBeGreaterThan(0);
        expect(p.stock).toBeGreaterThanOrEqualTo(0);
      });
    });
  });

  describe('setSearch / filtered', () => {
    it('should return all products when search is empty', () => {
      service.setSearch('');
      expect(service.filtered().length).toBe(12);
    });

    it('should filter products by name (case-insensitive)', () => {
      service.setSearch('iphone');
      const results = service.filtered();
      expect(results.length).toBeGreaterThan(0);
      results.forEach(p => expect(p.name.toLowerCase()).toContain('iphone'));
    });

    it('should filter products by tag', () => {
      service.setSearch('ANC');
      const results = service.filtered();
      expect(results.length).toBeGreaterThan(0);
      results.forEach(p =>
        expect(p.tags?.some(t => t.toLowerCase().includes('anc'))).toBeTrue()
      );
    });

    it('should return empty array for unmatched search', () => {
      service.setSearch('productoquenoexiste12345');
      expect(service.filtered().length).toBe(0);
    });
  });

  describe('setCategory / filtered', () => {
    it('should return all products when category is "all"', () => {
      service.setCategory('all');
      expect(service.filtered().length).toBe(12);
    });

    it('should filter by smartphones category', () => {
      service.setCategory('smartphones');
      const results = service.filtered();
      expect(results.length).toBeGreaterThan(0);
      results.forEach(p => expect(p.category).toBe('smartphones'));
    });

    it('should filter by audio category', () => {
      service.setCategory('audio');
      service.filtered().forEach(p => expect(p.category).toBe('audio'));
    });

    it('should return empty for unknown category', () => {
      service.setCategory('categoria_inexistente');
      expect(service.filtered().length).toBe(0);
    });
  });

  describe('combined search + category', () => {
    it('should apply both filters simultaneously', () => {
      service.setCategory('smartphones');
      service.setSearch('samsung');
      const results = service.filtered();
      expect(results.length).toBeGreaterThan(0);
      results.forEach(p => {
        expect(p.category).toBe('smartphones');
        expect(p.name.toLowerCase()).toContain('samsung');
      });
    });
  });

  describe('getById()', () => {
    it('should return product with matching id', () => {
      const product = service.getById(1);
      expect(product).toBeDefined();
      expect(product!.id).toBe(1);
    });

    it('should return undefined for non-existent id', () => {
      expect(service.getById(9999)).toBeUndefined();
    });
  });

  describe('getFeatured()', () => {
    it('should return at most 6 products', () => {
      expect(service.getFeatured().length).toBeLessThanOrEqualTo(6);
    });

    it('should only return isHot or isNew products', () => {
      service.getFeatured().forEach(p => {
        expect(p.isHot || p.isNew).toBeTrue();
      });
    });
  });

  describe('getByCategory()', () => {
    it('should return products of the given category', () => {
      const gamingProducts = service.getByCategory('gaming');
      expect(gamingProducts.length).toBeGreaterThan(0);
      gamingProducts.forEach(p => expect(p.category).toBe('gaming'));
    });

    it('should return empty array for unknown category', () => {
      expect(service.getByCategory('xyz_fake')).toEqual([]);
    });
  });

  describe('categories', () => {
    it('should include "all" as first category', () => {
      expect(service.categories[0]).toBe('all');
    });

    it('should contain expected categories', () => {
      expect(service.categories).toContain('smartphones');
      expect(service.categories).toContain('gaming');
      expect(service.categories).toContain('audio');
    });
  });
});
