import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CompareService } from './compare.service';
import { AmazonProduct } from '../models';

const mockProduct: AmazonProduct = {
  asin: 'B0CHX1W1XY',
  title: 'Apple iPhone 15',
  price: 799.99,
  url: 'https://amazon.com/dp/B0CHX1W1XY',
  imageUrl: 'https://img.jpg',
  rating: 4.8,
  reviewCount: 1000,
};

const mockProduct2: AmazonProduct = {
  asin: 'B0CMDRCZBX',
  title: 'Samsung Galaxy S24',
  price: 999.99,
  url: 'https://amazon.com/dp/B0CMDRCZBX',
  rating: 4.7,
  reviewCount: 500,
};

describe('CompareService', () => {
  let service: CompareService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(CompareService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('initial state', () => {
    it('should start not loading', () => {
      expect(service.loading()).toBeFalse();
    });

    it('should start with empty amItems', () => {
      expect(service.amItems().length).toBe(0);
    });

    it('should start with empty reviews', () => {
      expect(service.reviews().length).toBe(0);
    });

    it('should start with no error', () => {
      expect(service.error()).toBe('');
    });
  });

  describe('buscar()', () => {
    it('should do nothing for empty string', () => {
      service.buscar('   ');
      httpMock.expectNone('https://gpcueb.com/unishop/producto/buscar');
    });

    it('should call GET /producto/buscar with encoded nombre', () => {
      service.buscar('iPhone 15');
      const req = httpMock.expectOne(r => r.url.includes('/producto/buscar'));
      expect(req.request.method).toBe('GET');
      expect(req.request.url).toContain('iPhone%2015');
      req.flush({ amazon: [mockProduct] });
    });

    it('should populate amItems on success', () => {
      service.buscar('iPhone');
      httpMock.expectOne(r => r.url.includes('/producto/buscar'))
        .flush({ amazon: [mockProduct, mockProduct2] });
      expect(service.amItems().length).toBe(2);
      expect(service.loading()).toBeFalse();
    });

    it('should set error message on HTTP failure', () => {
      service.buscar('iPhone');
      httpMock.expectOne(r => r.url.includes('/producto/buscar'))
        .error(new ErrorEvent('network error'));
      expect(service.error()).toContain('gpcueb.com/unishop');
      expect(service.loading()).toBeFalse();
    });

    it('should clear previous results before a new search', () => {
      service.buscar('iPhone');
      httpMock.expectOne(r => r.url.includes('/producto/buscar'))
        .flush({ amazon: [mockProduct] });

      service.buscar('Samsung');
      expect(service.amItems().length).toBe(0); // reset before response
      httpMock.expectOne(r => r.url.includes('/producto/buscar'))
        .flush({ amazon: [mockProduct2] });
      expect(service.amItems().length).toBe(1);
    });
  });

  describe('cargarResenas()', () => {
    it('should call GET /producto/resenas with asin', () => {
      service.cargarResenas(mockProduct);
      const req = httpMock.expectOne(r => r.url.includes('/producto/resenas'));
      expect(req.request.method).toBe('GET');
      expect(req.request.url).toContain('B0CHX1W1XY');
      req.flush([{ author: 'John', rating: 5, body: 'Great!' }]);
    });

    it('should set reviewsFor to the product', () => {
      service.cargarResenas(mockProduct);
      httpMock.expectOne(r => r.url.includes('/producto/resenas'))
        .flush([]);
      expect(service.reviewsFor()?.asin).toBe('B0CHX1W1XY');
    });

    it('should set errorReviews on failure', () => {
      service.cargarResenas(mockProduct);
      httpMock.expectOne(r => r.url.includes('/producto/resenas'))
        .error(new ErrorEvent('error'));
      expect(service.errorReviews()).toContain('reseñas');
    });
  });

  describe('cerrarResenas()', () => {
    it('should clear reviews and reviewsFor', () => {
      service.cargarResenas(mockProduct);
      httpMock.expectOne(r => r.url.includes('/producto/resenas'))
        .flush([{ author: 'Ana', rating: 4 }]);
      service.cerrarResenas();
      expect(service.reviews().length).toBe(0);
      expect(service.reviewsFor()).toBeNull();
    });
  });

  describe('cheapest', () => {
    it('should return null when amItems is empty', () => {
      expect(service.cheapest).toBeNull();
    });

    it('should return the product with the lowest price', () => {
      service.buscar('test');
      httpMock.expectOne(r => r.url.includes('/producto/buscar'))
        .flush({ amazon: [mockProduct, mockProduct2] });
      expect(service.cheapest?.asin).toBe('B0CHX1W1XY'); // 799.99 < 999.99
    });

    it('should ignore products with price 0', () => {
      const freeProduct: AmazonProduct = { ...mockProduct, price: 0 };
      service.buscar('test');
      httpMock.expectOne(r => r.url.includes('/producto/buscar'))
        .flush({ amazon: [freeProduct, mockProduct2] });
      expect(service.cheapest?.asin).toBe('B0CMDRCZBX');
    });
  });

  describe('isCheapest()', () => {
    it('should return true for the cheapest product', () => {
      service.buscar('test');
      httpMock.expectOne(r => r.url.includes('/producto/buscar'))
        .flush({ amazon: [mockProduct, mockProduct2] });
      expect(service.isCheapest(mockProduct)).toBeTrue();
      expect(service.isCheapest(mockProduct2)).toBeFalse();
    });
  });

  describe('formatUSD()', () => {
    it('should format price with dollar sign', () => {
      expect(service.formatUSD(1234.56)).toBe('$1,234.56');
    });

    it('should return "Ver precio" for 0', () => {
      expect(service.formatUSD(0)).toBe('Ver precio');
    });
  });

  describe('stars()', () => {
    it('should return 5 full stars for rating 5', () => {
      expect(service.stars(5)).toBe('★★★★★');
    });

    it('should return 5 empty stars for undefined rating', () => {
      expect(service.stars(undefined)).toBe('☆☆☆☆☆');
    });

    it('should return mixed stars for rating 3', () => {
      expect(service.stars(3)).toBe('★★★☆☆');
    });
  });
});
