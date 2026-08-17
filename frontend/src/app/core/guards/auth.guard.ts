import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';

/** Bloqueia rotas internas quando não há sessão; redireciona para o login. */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isAutenticado() ? true : router.parseUrl('/login');
};
