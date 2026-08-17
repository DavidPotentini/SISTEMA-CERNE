import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import {
  AtividadePlanejada,
  Planejamento,
  PlanejamentoAtual,
  PlanProcesso,
} from '../../../models/planejamento/planejamento.model';

/**
 * Planejamento institucional do ciclo ativo (tenant vem do JWT). No máx. um planejamento vigente por
 * ciclo; "Gerar de modelo" substitui o anterior. A estrutura de processos/práticas é herdada da
 * metodologia base do modelo; aqui as atividades são ajustadas e complementares podem ser incluídas.
 */
@Injectable({ providedIn: 'root' })
export class PlanejamentoService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/planejamento`;

  /** Incrementa a cada mutação; situação e estrutura observam para recarregar. */
  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  /** Situação do ciclo ativo + planejamento vigente (ou `null`). */
  atual() {
    return this.http.get<PlanejamentoAtual>(this.base);
  }

  /** Estrutura (processos/práticas da metodologia base) com as atividades planejadas. */
  estrutura() {
    return this.http.get<PlanProcesso[]>(`${this.base}/estrutura`);
  }

  /** Gera o planejamento do ciclo ativo a partir do modelo (substitui o vigente, se houver). */
  gerar(modCod: number) {
    return this.http.post<Planejamento>(`${this.base}/gerar`, null, {
      params: { modCod },
    });
  }

  adicionarComplementar(prtCod: number, dto: Partial<AtividadePlanejada>) {
    return this.http.post<AtividadePlanejada>(`${this.base}/praticas/${prtCod}/atividades`, dto);
  }

  ajustarAtividade(atpCod: number, dto: Partial<AtividadePlanejada>) {
    return this.http.put<AtividadePlanejada>(`${this.base}/atividades/${atpCod}`, dto);
  }

  removerComplementar(atpCod: number) {
    return this.http.delete<void>(`${this.base}/atividades/${atpCod}`);
  }
}
