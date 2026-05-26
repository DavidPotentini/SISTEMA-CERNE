import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { Role } from '../../models/auth/auth.model';

/**
 * Bloqueia rotas cujo `data.roles` não inclui o papel da sessão. Espelha as
 * regras de `SecurityConfig` do backend para que o usuário não chegue a uma
 * tela cujas chamadas seriam todas rejeitadas com 403.
 */
export const roleGuard: CanActivateFn = (route) => {
  const router = inject(Router);
  const sessao = inject(AuthService).getSessao();

  if (!sessao) return router.parseUrl('/login');

  const rolesPermitidas = route.data?.['roles'] as Role[] | undefined;
  if (!rolesPermitidas || rolesPermitidas.length === 0) return true;

  return rolesPermitidas.includes(sessao.role) ? true : router.parseUrl('/incubadas');
};
