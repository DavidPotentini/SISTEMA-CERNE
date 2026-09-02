import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import {
  AtividadePlanejada,
  PlanejamentoAtual,
  PlanProcesso,
} from '../../../models/planejamento/planejamento.model';

/**
 * Planejamento institucional do ciclo ativo (tenant vem do JWT). No máx. um planejamento vigente por
 * ciclo; ele é materializado pelo "Gerar do ciclo" (Metodologia). Aqui as atividades são ajustadas e
 * complementares podem ser incluídas/removidas.
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

  adicionarComplementar(prtcCod: number, dto: Partial<AtividadePlanejada>) {
    return this.http.post<AtividadePlanejada>(`${this.base}/praticas/${prtcCod}/atividades`, dto);
  }

  ajustarAtividade(atpCod: number, dto: Partial<AtividadePlanejada>) {
    return this.http.put<AtividadePlanejada>(`${this.base}/atividades/${atpCod}`, dto);
  }

  removerAtividade(atpCod: number) {
    return this.http.delete<void>(`${this.base}/atividades/${atpCod}`);
  }

  /** Reordena as atividades de uma prática (arrastar-e-soltar): envia a sequência de `atpCod`. */
  reordenarAtividades(prtcCod: number, atpCods: number[]) {
    return this.http.put<void>(`${this.base}/praticas/${prtcCod}/atividades/ordem`, atpCods);
  }

  /** Reordena os agrupamentos de uma prática (arrastar-e-soltar): envia a sequência de `agrcCod`. */
  reordenarAgrupamentos(prtcCod: number, agrcCods: number[]) {
    return this.http.put<void>(`${this.base}/praticas/${prtcCod}/agrupamentos/ordem`, agrcCods);
  }
}
