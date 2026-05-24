import { Component, signal, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { ProductService } from '../../services/product.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  mobileOpen = signal(false);
  scrolled   = signal(false);

  readonly catLabels: Record<string, string> = {
    all: '🌐 Todos', smartphones: '📱 Smartphones', tablets: '📟 Tablets',
    gaming: '🎮 Gaming', audio: '🎧 Audio', monitores: '🖥️ Monitores',
    componentes: '⚙️ Componentes', almacenamiento: '💾 Almacenamiento',
    streaming: '📡 Streaming', accesorios: '🔌 Accesorios', muebles: '🪑 Muebles'
  };

  constructor(
    public auth: AuthService,
    public cart: CartService,
    public products: ProductService,
    private notify: NotificationService,
    private router: Router,
  ) {}

  @HostListener('window:scroll')
  onScroll() { this.scrolled.set(window.scrollY > 60); }

  goToCompare() { this.router.navigate(['/compare']); }

  logout() {
    this.auth.logout();
    this.notify.info('Sesión cerrada. ¡Hasta pronto!');
    this.router.navigate(['/login']);
  }

  toggleMobile() { this.mobileOpen.update(v => !v); }

  get userInitial(): string  { return this.auth.user()?.nombreUsuario?.charAt(0)?.toUpperCase() ?? '?'; }
  get userFirstName(): string { return this.auth.user()?.nombreUsuario ?? ''; }
}
