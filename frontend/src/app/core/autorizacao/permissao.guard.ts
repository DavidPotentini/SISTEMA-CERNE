import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { PermissaoService } from './permissao.service';
import { ENivel, ERecurso } from '../../enums/autorizacao';

/**
 * Bloqueia a rota quando o papel logado não tem o nível exigido no recurso —
 * espelha o interceptor `@RequerPermissao` do backend para o usuário não chegar a
 * uma tela cujas chamadas seriam todas 403.
 *
 * Configuração por rota:
 * ```ts
 * { path: 'planejamento', canActivate: [permissaoGuard],
 *   data: { recurso: ERecurso.Planejamento, nivel: ENivel.Leitura }, ... }
 * ```
 */
export const permissaoGuard: CanActivateFn = route => {
  const router = inject(Router);
  const auth = inject(AuthService);
  const perm = inject(PermissaoService);

  if (!auth.isAutenticado()) return router.parseUrl('/login');

  const recurso = route.data?.['recurso'] as ERecurso | undefined;
  const nivel = (route.data?.['nivel'] as ENivel) ?? ENivel.Leitura;

  if (recurso && !perm.permite(recurso, nivel)) return router.parseUrl('/home');

  return true;
};
