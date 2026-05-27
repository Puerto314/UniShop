import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('initial state', () => {
    it('should have no user when sessionStorage is empty', () => {
      expect(service.user()).toBeNull();
    });

    it('should not be logged in initially', () => {
      expect(service.isLoggedIn()).toBeFalsy();
    });

    it('should not be admin initially', () => {
      expect(service.isAdmin()).toBeFalsy();
    });
  });

  describe('loginBackend()', () => {
    it('should set user and token on successful login', () => {
      service.loginBackend('testuser', '1234').subscribe();

      const req = httpMock.expectOne('https://gpcueb.org/unishop/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({
        nombreUsuario: 'testuser',
        contraseniaUsuario: '1234',
      });

      req.flush({ token: 'abc123', nombreUsuario: 'testuser', rol: 'ROLE_USER' });

      expect(service.user()?.nombreUsuario).toBe('testuser');
      expect(service.isLoggedIn()).toBeTruthy();
      expect(service.isAdmin()).toBeFalsy();
    });

    it('should set isAdmin true when rol is ROLE_ADMIN', () => {
      service.loginBackend('admin', '1234').subscribe();
      httpMock.expectOne('https://gpcueb.org/unishop/auth/login')
        .flush({ token: 'tok', nombreUsuario: 'admin', rol: 'ROLE_ADMIN' });

      expect(service.isAdmin()).toBeTruthy;
    });

    it('should store token in sessionStorage', () => {
      service.loginBackend('u', 'p').subscribe();
      httpMock.expectOne('https://gpcueb.org/unishop/auth/login')
        .flush({ token: 'mytoken', nombreUsuario: 'u', rol: 'ROLE_USER' });

      expect(service.getToken()).toBe('mytoken');
    });

    it('should propagate error on failed login', (done) => {
      service.loginBackend('bad', 'bad').subscribe({
        error: err => {
          expect(err.status).toBe(401);
          // @ts-ignore
          done();
        }
      });
      httpMock.expectOne('https://gpcueb.org/unishop/auth/login')
        .flush({ error: 'Credenciales inválidas' }, { status: 401, statusText: 'Unauthorized' });
    });
  });

  describe('logout()', () => {
    it('should clear user and token', () => {
      service.loginBackend('u', 'p').subscribe();
      httpMock.expectOne('https://gpcueb.org/unishop/auth/login')
        .flush({ token: 'tok', nombreUsuario: 'u', rol: 'ROLE_USER' });

      service.logout();

      expect(service.user()).toBeNull();
      expect(service.isLoggedIn()).toBeFalsy();
      expect(service.getToken()).toBeNull();
    });
  });

  describe('enviarCodigo()', () => {
    it('should POST to /auth/enviar-codigo', () => {
      service.enviarCodigo('user', 'pass', 'user@gmail.com').subscribe();
      const req = httpMock.expectOne('https://gpcueb.org/unishop/auth/enviar-codigo');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({
        nombreUsuario: 'user',
        contraseniaUsuario: 'pass',
        correoElectronico: 'user@gmail.com',
      });
      req.flush({});
    });
  });

  describe('verificarYRegistrar()', () => {
    it('should POST to /auth/verificar-y-registrar', () => {
      service.verificarYRegistrar('user', 'pass', 'user@gmail.com', '123456').subscribe();
      const req = httpMock.expectOne('https://gpcueb.org/unishop/auth/verificar-y-registrar');
      expect(req.request.method).toBe('POST');
      expect(req.request.body.codigo).toBe('123456');
      req.flush({});
    });
  });

  describe('getToken()', () => {
    it('should return null when no token', () => {
      expect(service.getToken()).toBeNull();
    });
  });
});
