import { TestBed } from '@angular/core/testing';
import {
  HttpClient,
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from '../services/auth.service';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let authSpy: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authSpy = jasmine.createSpyObj('AuthService', ['getToken']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authSpy },
      ],
    });

    http     = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should add Authorization header for protected endpoints', () => {
    authSpy.getToken.and.returnValue('mytoken123');
    http.get('https://gpcueb.com/unishop/ordenes/mias').subscribe();

    const req = httpMock.expectOne('https://gpcueb.com/unishop/ordenes/mias');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mytoken123');
    req.flush({});
  });

  it('should NOT add Authorization header when no token', () => {
    authSpy.getToken.and.returnValue(null);
    http.get('https://gpcueb.com/unishop/ordenes/mias').subscribe();

    const req = httpMock.expectOne('https://gpcueb.com/unishop/ordenes/mias');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });

  it('should NOT add Authorization header for /auth/login (public)', () => {
    authSpy.getToken.and.returnValue('mytoken123');
    http.post('https://gpcueb.com/unishop/auth/login', {}).subscribe();

    const req = httpMock.expectOne('https://gpcueb.com/unishop/auth/login');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });

  it('should NOT add Authorization header for /auth/enviar-codigo (public)', () => {
    authSpy.getToken.and.returnValue('tok');
    http.post('https://gpcueb.com/unishop/auth/enviar-codigo', {}).subscribe();

    const req = httpMock.expectOne('https://gpcueb.com/unishop/auth/enviar-codigo');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });

  it('should NOT add Authorization header for /auth/verificar-y-registrar (public)', () => {
    authSpy.getToken.and.returnValue('tok');
    http.post('https://gpcueb.com/unishop/auth/verificar-y-registrar', {}).subscribe();

    const req = httpMock.expectOne('https://gpcueb.com/unishop/auth/verificar-y-registrar');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });
});
