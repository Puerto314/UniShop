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
}

export interface CartItem {
  product: ProductItem;
  qty: number;
}

export interface User {
  id: number;
  name: string;
  email: string;
  role: 'admin' | 'client';
  avatar?: string;
  joinDate: string;
}

export interface Order {
  id: string;
  date: string;
  items: CartItem[];
  total: number;
  status: 'pending' | 'processing' | 'shipped' | 'delivered' | 'cancelled';
}

export interface CouponResult {
  success: boolean;
  message: string;
  discount?: number;
}

export interface ToastMessage {
  id: number;
  type: 'success' | 'error' | 'info' | 'warning';
  message: string;
}

// ── Modelos Amazon ────────────────────────────────────────────────────────────

/** Un producto devuelto por el backend desde Amazon.com (precio en USD). */
export interface AmazonProduct {
  asin: string;
  title: string;
  /** Precio en USD (ej: 12.99). 0 = no disponible. */
  price: number;
  url: string;
  imageUrl?: string;
  rating?: number;
  reviewCount?: number;
}

/** Respuesta del endpoint GET /producto/buscar?nombre=... */
export interface BuscarResponse {
  amazon: AmazonProduct[];
}

/** Una reseña devuelta por GET /producto/resenas?asin=... */
export interface AmazonReview {
  author: string;
  title?: string;
  body?: string;
  rating?: number;
  date?: string;
}
