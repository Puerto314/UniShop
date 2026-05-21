import { Component, OnInit, signal, computed, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { trigger, transition, style, animate, query, stagger } from '@angular/animations';
import { ProductService } from '../services/product.service';
import { CartService } from '../services/cart.service';
import { NotificationService } from '../services/notification.service';
import { ProductCard } from '../shared/product-card/product-card';
import { ProductItem } from '../models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, ProductCard],
  templateUrl: './home.html',
  styleUrl: './home.css',
  animations: [
    trigger('staggerGrid', [
      transition('* => *', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(50, animate('280ms ease', style({ opacity: 1, transform: 'translateY(0)' })))
        ], { optional: true })
      ])
    ])
  ]
})
export class Home implements OnInit, OnDestroy {
  carouselIdx = signal(0);
  heroBanners = [
    { tag: 'OFERTA LIMITADA', title: 'iPhone 15', sub: 'Dynamic Island, USB-C y cámara de 48MP. El iPhone más avanzado.', color: 'var(--magenta)', cta: 'Ver Oferta', img: '📱' },
    { tag: 'NUEVO STOCK',     title: 'Galaxy S24 Ultra', sub: 'S Pen integrado, 200MP y titanio. El rey de los Android.', color: 'var(--cyan)',    cta: 'Comprar Ahora', img: '🌟' },
    { tag: 'BEST SELLER',     title: 'Sony WH-1000XM5', sub: 'Los mejores auriculares con cancelación de ruido del mercado.', color: 'var(--yellow)', cta: 'Ver Oferta', img: '🎧' },
  ];
  private carouselTimer: any;

  sortBy      = signal<'default'|'price-asc'|'price-desc'|'rating'|'newest'>('default');
  wishlist    = signal<number[]>([]);
  quickViewProduct = signal<ProductItem | null>(null);
  showFilters = signal(false);
  priceMax    = signal(5000000);
  hotOnly     = signal(false);
  newOnly     = signal(false);

  sortedProducts = computed(() => {
    let list = [...this.products.filtered()].filter(p => p.price <= this.priceMax());
    if (this.hotOnly()) list = list.filter(p => p.isHot);
    if (this.newOnly()) list = list.filter(p => p.isNew);
    switch (this.sortBy()) {
      case 'price-asc':  return list.sort((a,b) => a.price - b.price);
      case 'price-desc': return list.sort((a,b) => b.price - a.price);
      case 'rating':     return list.sort((a,b) => b.rating - a.rating);
      case 'newest':     return list.filter(p => p.isNew).concat(list.filter(p => !p.isNew));
      default:           return list;
    }
  });

  readonly catIcon: Record<string,string> = {
    all:'🌐', smartphones:'📱', tablets:'📟', gaming:'🎮', audio:'🎧',
    monitores:'🖥️', componentes:'⚙️', almacenamiento:'💾',
    streaming:'📡', accesorios:'🔌', muebles:'🪑'
  };
  readonly catLabel: Record<string,string> = {
    all:'Todos', smartphones:'Smartphones', tablets:'Tablets', gaming:'Gaming',
    audio:'Audio', monitores:'Monitores', componentes:'Componentes',
    almacenamiento:'Almacenamiento', streaming:'Streaming',
    accesorios:'Accesorios', muebles:'Muebles'
  };

  constructor(
    public products: ProductService,
    public cart: CartService,
    private notify: NotificationService,
  ) {}

  ngOnInit() {
    this.carouselTimer = setInterval(() => {
      this.carouselIdx.update(i => (i + 1) % this.heroBanners.length);
    }, 4500);
  }
  ngOnDestroy() { clearInterval(this.carouselTimer); }

  nextSlide() { this.carouselIdx.update(i => (i + 1) % this.heroBanners.length); }
  prevSlide() { this.carouselIdx.update(i => (i - 1 + this.heroBanners.length) % this.heroBanners.length); }

  openAmazon(product: ProductItem) {
    if (product.amazonUrl) window.open(product.amazonUrl, '_blank');
  }

  onAddToCart(product: ProductItem) {
    this.cart.addToCart(product);
    this.notify.success(`${product.name} agregado al carrito`);
    this.cart.openCart();
  }

  onWishlist(product: ProductItem) {
    this.wishlist.update(w =>
      w.includes(product.id) ? w.filter(id => id !== product.id) : [...w, product.id]
    );
    const inList = this.wishlist().includes(product.id);
    this.notify.info(inList ? `${product.name} en favoritos` : `Removido de favoritos`);
  }

  onQuickView(product: ProductItem) { this.quickViewProduct.set(product); }
  closeQuickView()                  { this.quickViewProduct.set(null); }

  addQuickViewToCart() {
    const p = this.quickViewProduct();
    if (p) { this.onAddToCart(p); this.closeQuickView(); }
  }

  filterHot()   { this.hotOnly.set(true);  this.newOnly.set(false); }
  filterNew()   { this.newOnly.set(true);  this.hotOnly.set(false); }
  clearFilter() { this.hotOnly.set(false); this.newOnly.set(false); }

  formatPrice(p: number) { return '$' + p.toLocaleString('es-CO'); }
  discount(p: ProductItem) {
    if (!p.originalPrice) return 0;
    return Math.round((1 - p.price / p.originalPrice) * 100);
  }
  trackFn(_: number, p: ProductItem) { return p.id; }
}
