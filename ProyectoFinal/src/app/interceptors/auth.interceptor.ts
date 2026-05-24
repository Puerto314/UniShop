import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

// Rutas públicas que NO deben llevar token (aunque exista uno en sesión)
const PUBLIC_PATHS = ['/auth/login', '/auth/enviar-codigo', '/auth/verificar-y-registrar'];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.getToken();

  const isPublic = PUBLIC_PATHS.some(path => req.url.includes(path));

  if (token && !isPublic) {
    const cloned = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next(cloned);
  }
  return next(req);
};
