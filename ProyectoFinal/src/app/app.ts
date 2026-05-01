import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './shared/navbar/navbar';
import { Cart } from './cart/cart';
import { Toast } from './shared/toast/toast';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, Navbar, Cart, Toast],
  template: `
    <div class="scanlines">
      <app-navbar *ngIf="auth.isLoggedIn()"></app-navbar>
      <router-outlet></router-outlet>
      <app-cart></app-cart>
      <app-toast></app-toast>
    </div>
  `,
})
export class App {
  constructor(public auth: AuthService) {}
}
