export interface ProductItem {
  id: number;
  name: string;
  price: number;
  originalPrice?: number;
  image: string;
  category: string;
  rating: number;
  reviews: number;
  stock: number;
  tags?: string[];
  isNew?: boolean;
  isHot?: boolean;
  amazonUrl?: string;
  asin?: string;
}

export interface CartItem {
  product: ProductItem;
  qty: number;
}

export interface User {
  id?: number;
  nombreUsuario: string;
  rol: string;
  avatar?: string;
  joinDate?: string;
}

export interface Order {
  id: string;
  date: string;
  items: CartItem[];
  total: number;
  status: 'pending' | 'processing' | 'shipped' | 'delivered' | 'cancelled';
}

export interface ToastMessage {
  id: number;
  type: 'success' | 'error' | 'info' | 'warning';
  message: string;
}

// ── Modelos Amazon ─────────────────────────────────────────────────────────────

export interface AmazonProduct {
  asin: string;
  title: string;
  price: number;
  url: string;
  imageUrl?: string;
  rating?: number;
  reviewCount?: number;
}

export interface BuscarResponse {
  amazon: AmazonProduct[];
}

export interface AmazonReview {
  author: string;
  title?: string;
  body?: string;
  rating?: number;
  date?: string;
}

export interface AdminUser {
  id: number;
  nombreUsuario: string;
  contraseniaUsuario: string;
  codigoAdmin: string;
}

export interface ClienteUser {
  id: number;
  nombreUsuario: string;
  contraseniaUsuario: string;
  correoElectronico: string;
}
