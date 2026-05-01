import {Component, signal, computed, HostListener} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {CartService} from '../../services/cart.service';
import {ProductService} from '../../services/product.service';
import {NotificationService} from '../../services/notification.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  searchQuery = '';
  mobileOpen = signal(false);
  scrolled = signal(false);

  constructor(
    public auth: AuthService,
    public cart: CartService,
    public products: ProductService,
    private notify: NotificationService,
  ) {
  }

  @HostListener('window:scroll')
  onScroll() {
    this.scrolled.set(window.scrollY > 60);
  }

  search() {
    this.products.setSearch(this.searchQuery);
  }

  clearSearch() {
    this.searchQuery = '';
    this.products.setSearch('');
  }

  logout() {
    this.auth.logout();
    this.notify.info('Sesión cerrada. ¡Hasta pronto!');
  }

  toggleMobile() {
    this.mobileOpen.update(v => !v);
  }

  get userInitial(): string {
    return this.auth.user()?.name?.charAt(0) ?? '';
  }

  get userFirstName(): string {
    return this.auth.user()?.name?.split(' ')[0] ?? '';
  }
}
