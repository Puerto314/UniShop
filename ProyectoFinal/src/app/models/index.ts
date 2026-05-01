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
