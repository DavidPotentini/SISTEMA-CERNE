import { Injectable, computed, inject } from '@angular/core';
import { AuthService } from '../services/auth/auth.service';
import { ENivel, ERecurso } from '../../enums/autorizacao';

/** Ordem dos níveis: um nível "cobre" todos os abaixo dele. */
const RANK: Record<ENivel, number> = {
  [ENivel.Nenhum]: 0,
  [ENivel.Leitura]: 1,
  [ENivel.Edicao]: 2,
  [ENivel.Total]: 3,
};

/**
 * Substitui o `roleGuard` antigo (baseado em papéis fixos ADMIN_*). Lê a matriz
 * `permissoes` do papel logado (papel × recurso) e responde se o usuário atinge o
 * nível mínimo exigido para um recurso.
 */
@Injectable({ providedIn: 'root' })
export class PermissaoService {
  private readonly auth = inject(AuthService);

  readonly permissoes = computed(() => this.auth.sessao()?.permissoes ?? {});

  permite(recurso: ERecurso, minimo: ENivel = ENivel.Leitura): boolean {
    const atual = this.permissoes()[recurso] ?? ENivel.Nenhum;
    return RANK[atual] >= RANK[minimo];
  }
}
