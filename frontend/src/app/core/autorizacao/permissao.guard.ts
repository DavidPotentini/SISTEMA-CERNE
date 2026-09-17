import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { PermissaoService } from './permissao.service';
import { ENivel, ERecurso } from '../../enums/autorizacao';

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
