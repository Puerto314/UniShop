import {Component, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {trigger, transition, style, animate, keyframes} from '@angular/animations';
import {AuthService} from '../services/auth.service';
import {NotificationService} from '../services/notification.service';

const DOMINIOS_PERMITIDOS = ['@gmail.com', '@hotmail.com', '@unbosque.edu.co'];

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
  animations: [
    trigger('formAnim', [
      transition(':enter', [
        style({opacity: 0, transform: 'translateY(20px)'}),
        animate('320ms ease', style({opacity: 1, transform: 'translateY(0)'}))
      ]),
      transition(':leave', [
        animate('200ms ease', style({opacity: 0, transform: 'translateY(-10px)'}))
      ])
    ]),
    trigger('shake', [
      transition(':enter', [
        animate('420ms ease', keyframes([
          style({transform: 'translateX(0)', offset: 0}),
          style({transform: 'translateX(-8px)', offset: .2}),
          style({transform: 'translateX(8px)', offset: .4}),
          style({transform: 'translateX(-6px)', offset: .6}),
          style({transform: 'translateX(6px)', offset: .8}),
          style({transform: 'translateX(0)', offset: 1}),
        ]))
      ])
    ])
  ]
})
export class Login {
  activeTab: 'login' | 'register' = 'login';
  showPassword = false;
  isLoading = signal(false);

  // ── Login ──────────────────────────────────────────────────────────────────
  loginUsername = '';
  loginPassword = '';
  rememberMe = false;
  loginError = '';

  // ── Register ───────────────────────────────────────────────────────────────
  regUsername = '';
  regEmail = '';
  regPassword = '';
  regConfirm = '';
  acceptTerms = false;
  registerError = '';

  registerStep: 1 | 2 = 1;
  verCode = '';
  verError = '';

  stats = [
    {value: '50K+', label: 'Clientes'},
    {value: '10K+', label: 'Productos'},
    {value: '4.9★', label: 'Calificación'},
    {value: '24/7', label: 'Soporte'},
  ];

  constructor(
    private auth: AuthService,
    private notify: NotificationService,
    private router: Router,
  ) {
  }

  get passwordStrength(): { score: number; label: string; color: string; pct: number } {
    const p = this.regPassword;
    let score = 0;
    if (p.length >= 8) score++;
    if (/[A-Z]/.test(p)) score++;
    if (/[0-9]/.test(p)) score++;
    if (/[^A-Za-z0-9]/.test(p)) score++;
    const levels = [
      {label: '', color: 'transparent'},
      {label: 'Débil', color: 'var(--red)'},
      {label: 'Regular', color: 'var(--yellow)'},
      {label: 'Buena', color: 'var(--cyan)'},
      {label: 'Fuerte', color: 'var(--green)'},
    ];
    return {score, pct: score * 25, ...levels[score]};
  }

  // ── Login ──────────────────────────────────────────────────────────────────

  login() {
    this.loginError = '';
    if (!this.loginUsername || !this.loginPassword) {
      this.loginError = 'Completa todos los campos';
      return;
    }
    this.isLoading.set(true);
    this.auth.loginBackend(this.loginUsername, this.loginPassword).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.notify.success(`Bienvenido, ${this.loginUsername}`);
        this.router.navigate(['/compare']);
      },
      error: (err) => {
        this.isLoading.set(false);
        const msg = err?.error?.error ?? err?.message ?? 'Error de conexión';
        this.loginError = msg === 'Credenciales inválidas' || err?.status === 401
          ? 'Usuario o contraseña incorrectos'
          : msg;
      }
    });
  }

  loginAsAdmin() {
    this.loginUsername = 'admin';
    this.loginPassword = '1234567890';
    this.login();
  }

  loginAsClient() {
    this.loginUsername = 'clientenormal';
    this.loginPassword = '1234567890';
    this.login();
  }

  forgotPassword() {
    this.notify.info('Contacta al administrador para recuperar tu contraseña.');
  }

  // ── Validación de dominio de correo ────────────────────────────────────────

  private dominioPermitido(email: string): boolean {
    const lower = email.trim().toLowerCase();
    return DOMINIOS_PERMITIDOS.some(d => lower.endsWith(d));
  }

  // ── Registro — Paso 1: enviar código ──────────────────────────────────────

  register() {
    this.registerError = '';
    if (!this.regUsername || !this.regEmail || !this.regPassword || !this.regConfirm) {
      this.registerError = 'Completa todos los campos';
      return;
    }
    if (this.regPassword !== this.regConfirm) {
      this.registerError = 'Las contraseñas no coinciden';
      return;
    }
 
    if (!this.dominioPermitido(this.regEmail)) {
      this.registerError = 'Solo se permiten correos @gmail.com, @hotmail.com o @unbosque.edu.co';
      return;
    }

    this.isLoading.set(true);
    this.auth.enviarCodigo(this.regUsername, this.regPassword, this.regEmail).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.notify.success('Código enviado a ' + this.regEmail);
        this.registerStep = 2;
      },
      error: (err) => {
        this.isLoading.set(false);
        const status = err?.status;
        if (status === 409) {
          this.registerError = `El usuario "${this.regUsername}" ya está en uso`;
        } else if (status === 400) {
          this.registerError = err?.error?.error ?? 'El correo electrónico no es válido';
        } else if (status === 503) {
          this.registerError = 'No se pudo enviar el correo. Verifica la dirección e intenta de nuevo.';
        } else {
          this.registerError = err?.error?.error ?? 'Error de conexión con el servidor';
        }
      }
    });
  }

  // ── Registro — Paso 2: verificar código y crear cuenta ────────────────────

  verificar() {
    this.verError = '';
    if (!this.verCode || this.verCode.trim().length < 4) {
      this.verError = 'Ingresa el código que recibiste';
      return;
    }
    this.isLoading.set(true);
    this.auth.verificarYRegistrar(this.regUsername, this.regPassword, this.regEmail, this.verCode.trim()).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.notify.success('¡Cuenta creada! Inicia sesión con tu usuario.');
        this.activeTab = 'login';
        this.loginUsername = this.regUsername;
        this.registerStep = 1;
        this.regUsername = this.regEmail = this.regPassword = this.regConfirm = this.verCode = '';
      },
      error: (err) => {
        this.isLoading.set(false);
        const status = err?.status;
        if (status === 400) {
          this.verError = err?.error?.error ?? 'Código incorrecto o expirado';
        } else if (status === 409) {
          this.verError = 'El usuario ya existe, inicia sesión directamente';
        } else {
          this.verError = err?.error?.error ?? 'Error de conexión';
        }
      }
    });
  }

  reenviarCodigo() {
    this.verCode = '';
    this.verError = '';
    this.registerStep = 1;
  }
}
