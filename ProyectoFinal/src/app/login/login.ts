import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { trigger, transition, style, animate, keyframes } from '@angular/animations';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
  animations: [
    trigger('formAnim', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('320ms ease', style({ opacity: 1, transform: 'translateY(0)' }))
      ]),
      transition(':leave', [
        animate('200ms ease', style({ opacity: 0, transform: 'translateY(-10px)' }))
      ])
    ]),
    trigger('shake', [
      transition(':enter', [
        animate('420ms ease', keyframes([
          style({ transform: 'translateX(0)',   offset: 0 }),
          style({ transform: 'translateX(-8px)', offset: .2 }),
          style({ transform: 'translateX(8px)',  offset: .4 }),
          style({ transform: 'translateX(-6px)', offset: .6 }),
          style({ transform: 'translateX(6px)',  offset: .8 }),
          style({ transform: 'translateX(0)',   offset: 1 }),
        ]))
      ])
    ])
  ]
})
export class Login {
  activeTab: 'login' | 'register' = 'login';
  showPassword = false;
  isLoading = signal(false);

  // Login
  loginEmail    = '';
  loginPassword = '';
  rememberMe    = false;
  loginError    = '';

  // Register
  regName     = '';
  regEmail    = '';
  regPassword = '';
  regConfirm  = '';
  acceptTerms = false;
  registerError = '';

  stats = [
    { value: '50K+', label: 'Clientes' },
    { value: '10K+', label: 'Productos' },
    { value: '4.9★', label: 'Calificación' },
    { value: '24/7', label: 'Soporte' },
  ];

  constructor(
    private auth: AuthService,
    private notify: NotificationService,
    private router: Router,
  ) {}

  get passwordStrength(): { score: number; label: string; color: string; pct: number } {
    const p = this.regPassword;
    let score = 0;
    if (p.length >= 8)         score++;
    if (/[A-Z]/.test(p))       score++;
    if (/[0-9]/.test(p))       score++;
    if (/[^A-Za-z0-9]/.test(p)) score++;
    const levels = [
      { label: '',        color: 'transparent' },
      { label: 'Débil',   color: 'var(--red)' },
      { label: 'Regular', color: 'var(--yellow)' },
      { label: 'Buena',   color: 'var(--cyan)' },
      { label: 'Fuerte',  color: 'var(--green)' },
    ];
    return { score, pct: score * 25, ...levels[score] };
  }

  login() {
    this.loginError = '';
    if (!this.loginEmail || !this.loginPassword) {
      this.loginError = 'Completa todos los campos'; return;
    }
    this.isLoading.set(true);
    setTimeout(() => {
      const r = this.auth.login(this.loginEmail, this.loginPassword);
      this.isLoading.set(false);
      if (r.success) {
        this.notify.success(r.message);
        this.router.navigate(['/home']);
      } else {
        this.loginError = r.message;
      }
    }, 900);
  }

  loginAsAdmin() {
    this.loginEmail    = 'admin@neon.co';
    this.loginPassword = 'Admin123!';
    this.login();
  }

  loginAsClient() {
    this.loginEmail    = 'demo@neon.co';
    this.loginPassword = 'Demo1234!';
    this.login();
  }

  forgotPassword() {
    if (!this.loginEmail) { this.loginError = 'Ingresa tu correo primero'; return; }
    this.notify.info(`Enlace de recuperación enviado a ${this.loginEmail}`);
  }

  register() {
    this.registerError = '';
    if (!this.regName || !this.regEmail || !this.regPassword || !this.regConfirm) {
      this.registerError = 'Completa todos los campos'; return;
    }
    if (this.regPassword !== this.regConfirm) {
      this.registerError = 'Las contraseñas no coinciden'; return;
    }
    if (!this.acceptTerms) {
      this.registerError = 'Debes aceptar los términos'; return;
    }
    this.isLoading.set(true);
    setTimeout(() => {
      this.isLoading.set(false);
      this.notify.success('¡Cuenta creada! Inicia sesión');
      this.activeTab = 'login';
      this.loginEmail = this.regEmail;
    }, 1200);
  }
}
