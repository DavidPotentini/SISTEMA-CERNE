import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth/auth.service';
import { environment } from '../../../environments/environment';

/** Adiciona o header X-Tenant em toda requisição ao backend quando há sessão ativa. */
export const tenantInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith(environment.apiUrl)) {
    return next(req);
  }

  const schema = inject(AuthService).getTenantSchema();

  if (schema) {
    req = req.clone({ setHeaders: { 'X-Tenant': schema } });
  }

  return next(req);
};
