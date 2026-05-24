import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Login } from './login';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let authSpy: jasmine.SpyObj<AuthService>;
  let notifySpy: jasmine.SpyObj<NotificationService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authSpy   = jasmine.createSpyObj('AuthService',   ['loginBackend', 'enviarCodigo', 'verificarYRegistrar']);
    notifySpy = jasmine.createSpyObj('NotificationService', ['success', 'error', 'info', 'warning']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [Login, NoopAnimationsModule],
      providers: [
        { provide: AuthService,          useValue: authSpy   },
        { provide: NotificationService,  useValue: notifySpy },
        { provide: Router,               useValue: routerSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ── passwordStrength ────────────────────────────────────────────────────────

  describe('passwordStrength', () => {
    it('should score 0 for empty password', () => {
      component.regPassword = '';
      expect(component.passwordStrength.score).toBe(0);
    });

    it('should score 1 for password with 8+ chars only', () => {
      component.regPassword = 'abcdefgh';
      expect(component.passwordStrength.score).toBe(1);
    });

    it('should score 4 for strong password', () => {
      component.regPassword = 'Abcd1234!';
      expect(component.passwordStrength.score).toBe(4);
    });

    it('should return label "Fuerte" for score 4', () => {
      component.regPassword = 'Abcd1234!';
      expect(component.passwordStrength.label).toBe('Fuerte');
    });

    it('should return pct 100 for score 4', () => {
      component.regPassword = 'Abcd1234!';
      expect(component.passwordStrength.pct).toBe(100);
    });
  });

  // ── login() ─────────────────────────────────────────────────────────────────

  describe('login()', () => {
    it('should set loginError when fields are empty', () => {
      component.loginUsername = '';
      component.loginPassword = '';
      component.login();
      expect(component.loginError).toBe('Completa todos los campos');
    });

    it('should call loginBackend with correct credentials', () => {
      authSpy.loginBackend.and.returnValue(of({}));
      component.loginUsername = 'admin';
      component.loginPassword = '1234567890';
      component.login();
      expect(authSpy.loginBackend).toHaveBeenCalledWith('admin', '1234567890');
    });

    it('should navigate to /compare on success', () => {
      authSpy.loginBackend.and.returnValue(of({}));
      component.loginUsername = 'user';
      component.loginPassword = 'pass';
      component.login();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/compare']);
    });

    it('should show success notification with username on login', () => {
      authSpy.loginBackend.and.returnValue(of({}));
      component.loginUsername = 'pepito';
      component.loginPassword = 'pass';
      component.login();
      expect(notifySpy.success).toHaveBeenCalledWith('Bienvenido, pepito');
    });

    it('should set loginError for 401 status', () => {
      authSpy.loginBackend.and.returnValue(throwError(() => ({ status: 401 })));
      component.loginUsername = 'x';
      component.loginPassword = 'y';
      component.login();
      expect(component.loginError).toBe('Usuario o contraseña incorrectos');
    });

    it('should set loading false after error', () => {
      authSpy.loginBackend.and.returnValue(throwError(() => ({ status: 500, message: 'Error' })));
      component.loginUsername = 'x';
      component.loginPassword = 'y';
      component.login();
      expect(component.isLoading()).toBeFalse();
    });
  });

  // ── register() ───────────────────────────────────────────────────────────────

  describe('register()', () => {
    it('should set error when fields are incomplete', () => {
      component.register();
      expect(component.registerError).toBe('Completa todos los campos');
    });

    it('should set error when passwords do not match', () => {
      component.regUsername = 'user';
      component.regEmail = 'user@gmail.com';
      component.regPassword = 'abc123';
      component.regConfirm = 'different';
      component.register();
      expect(component.registerError).toBe('Las contraseñas no coinciden');
    });

    it('should reject email with invalid domain', () => {
      component.regUsername = 'user';
      component.regEmail = 'user@yahoo.com';
      component.regPassword = 'abc123';
      component.regConfirm = 'abc123';
      component.register();
      expect(component.registerError).toContain('@gmail.com');
    });

    it('should accept valid email domains', () => {
      authSpy.enviarCodigo.and.returnValue(of({}));
      const validEmails = ['a@gmail.com', 'b@hotmail.com', 'c@unbosque.edu.co'];
      validEmails.forEach(email => {
        component.regUsername = 'user';
        component.regEmail = email;
        component.regPassword = 'pass';
        component.regConfirm = 'pass';
        component.registerError = '';
        component.register();
        expect(component.registerError).toBe('');
      });
    });

    it('should call enviarCodigo on valid input', () => {
      authSpy.enviarCodigo.and.returnValue(of({}));
      component.regUsername = 'user';
      component.regEmail = 'user@gmail.com';
      component.regPassword = 'pass';
      component.regConfirm = 'pass';
      component.register();
      expect(authSpy.enviarCodigo).toHaveBeenCalled();
    });

    it('should advance to step 2 on success', () => {
      authSpy.enviarCodigo.and.returnValue(of({}));
      component.regUsername = 'user';
      component.regEmail = 'user@gmail.com';
      component.regPassword = 'pass';
      component.regConfirm = 'pass';
      component.register();
      expect(component.registerStep).toBe(2);
    });

    it('should set error message for 409 (username taken)', () => {
      authSpy.enviarCodigo.and.returnValue(throwError(() => ({ status: 409 })));
      component.regUsername = 'taken';
      component.regEmail = 'taken@gmail.com';
      component.regPassword = 'pass';
      component.regConfirm = 'pass';
      component.register();
      expect(component.registerError).toContain('taken');
    });
  });

  // ── verificar() ──────────────────────────────────────────────────────────────

  describe('verificar()', () => {
    it('should set verError when code is empty', () => {
      component.verCode = '';
      component.verificar();
      expect(component.verError).toBeTruthy();
    });

    it('should set verError when code is too short', () => {
      component.verCode = 'ab';
      component.verificar();
      expect(component.verError).toBeTruthy();
    });

    it('should call verificarYRegistrar with trimmed code', () => {
      authSpy.verificarYRegistrar.and.returnValue(of({}));
      component.regUsername = 'u'; component.regPassword = 'p'; component.regEmail = 'e@gmail.com';
      component.verCode = '  123456  ';
      component.verificar();
      expect(authSpy.verificarYRegistrar).toHaveBeenCalledWith('u', 'p', 'e@gmail.com', '123456');
    });

    it('should switch to login tab and reset step on success', () => {
      authSpy.verificarYRegistrar.and.returnValue(of({}));
      component.verCode = '123456';
      component.verificar();
      expect(component.activeTab).toBe('login');
      expect(component.registerStep).toBe(1);
    });
  });

  // ── helpers ───────────────────────────────────────────────────────────────────

  describe('loginAsAdmin()', () => {
    it('should set admin credentials and call login', () => {
      authSpy.loginBackend.and.returnValue(of({}));
      component.loginAsAdmin();
      expect(component.loginUsername).toBe('admin');
      expect(authSpy.loginBackend).toHaveBeenCalledWith('admin', '1234567890');
    });
  });

  describe('forgotPassword()', () => {
    it('should show info notification', () => {
      component.forgotPassword();
      expect(notifySpy.info).toHaveBeenCalled();
    });
  });
});
