import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { Role } from '../../models/auth/auth.model';

/**
 * Bloqueia rotas cujo `data.roles` não inclui o papel da sessão. Espelha as
 * regras de `SecurityConfig` do backend para que o usuário não chegue a uma
 * tela cujas chamadas seriam todas rejeitadas com 403.
 *
 * Para `ADMIN_INCUBADA`, também impede que o usuário edite manualmente o
 * `:incCod` da URL para acessar dados de outra incubada.
 */
export const roleGuard: CanActivateFn = (route) => {
  const router = inject(Router);
  const sessao = inject(AuthService).getSessao();

  if (!sessao) return router.parseUrl('/login');

  const rolesPermitidas = route.data?.['roles'] as Role[] | undefined;
  if (rolesPermitidas && rolesPermitidas.length > 0 && !rolesPermitidas.includes(sessao.role)) {
    return router.parseUrl('/home');
  }

  if (sessao.role === 'ADMIN_INCUBADA' && sessao.incCod != null) {
    const incCodUrl = lerIncCod(route);
    if (incCodUrl != null && incCodUrl !== sessao.incCod) {
      return router.parseUrl('/home');
    }
  }

  return true;
};

function lerIncCod(route: ActivatedRouteSnapshot): number | null {
  let atual: ActivatedRouteSnapshot | null = route;
  while (atual) {
    const v = atual.paramMap.get('incCod');
    if (v != null) {
      const n = Number(v);
      return Number.isFinite(n) ? n : null;
    }
    atual = atual.parent;
  }
  return null;
}
