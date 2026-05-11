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

// ── Modelos para comparador externo ──────────────────────────────────────────

export interface ExternalProduct {
  nombreProducto: string;
  descripcionProducto: string;
  precioProducto: number;
  tienda: 'Amazon' | 'MercadoLibre';
  thumbnail?: string;
  permalink?: string;
}

export interface BuscarResponse {
  mercadoLibre: ExternalProduct[];
  mercadoLibreUrl: string;
  amazon: ExternalProduct[];
}

export interface MercadoLibreRawItem {
  id: string;
  title: string;
  price: number;
  permalink: string;
  thumbnail: string;
  currency_id: string;
  condition: string;
  available_quantity: number;
}

export interface MercadoLibreSearchResponse {
  results: MercadoLibreRawItem[];
}
