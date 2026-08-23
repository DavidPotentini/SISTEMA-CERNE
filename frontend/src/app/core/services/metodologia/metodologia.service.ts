import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import {
  EAtivoInativo,
  Indicador,
  Pratica,
  Processo,
} from '../../../models/metodologia/metodologia.model';

/**
 * Metodologia da própria incubadora (schema do tenant vem do JWT). Documento vivo, sem versionamento:
 * as abas listam e editam a metodologia direto.
 */
@Injectable({ providedIn: 'root' })
export class MetodologiaService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/metodologia`;

  /** Incrementa a cada mutação; a listagem de processos observa para recarregar. */
  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  listarProcessos() {
    return this.http.get<Processo[]>(`${this.base}/processos`);
  }

  criarProcesso(dto: Partial<Processo>) {
    return this.http.post<Processo>(`${this.base}/processos`, dto);
  }

  editarProcesso(prcCod: number, dto: Partial<Processo>) {
    return this.http.put<Processo>(`${this.base}/processos/${prcCod}`, dto);
  }

  /** Reordena os processos (arrastar-e-soltar): envia a sequência de `prcCod`. */
  reordenarProcessos(prcCods: number[]) {
    return this.http.put<Processo[]>(`${this.base}/processos/ordem`, prcCods);
  }

  /** Ativa/inativa o processo (inativo continua visível, mas fora da criação de modelos). */
  alterarSituacaoProcesso(prcCod: number, situacao: EAtivoInativo) {
    return this.http.patch<Processo>(`${this.base}/processos/${prcCod}/situacao`, null, {
      params: { situacao },
    });
  }

  adicionarPratica(prcCod: number, dto: Partial<Pratica>) {
    return this.http.post<Pratica>(`${this.base}/processos/${prcCod}/praticas`, dto);
  }

  editarPratica(prcCod: number, prtCod: number, dto: Partial<Pratica>) {
    return this.http.put<Pratica>(`${this.base}/processos/${prcCod}/praticas/${prtCod}`, dto);
  }

  alterarSituacaoPratica(prcCod: number, prtCod: number, situacao: EAtivoInativo) {
    return this.http.patch<Pratica>(
      `${this.base}/processos/${prcCod}/praticas/${prtCod}/situacao`,
      null,
      { params: { situacao } },
    );
  }

  // ---- indicadores ----

  listarIndicadores() {
    return this.http.get<Indicador[]>(`${this.base}/indicadores`);
  }

  criarIndicador(dto: Partial<Indicador>) {
    return this.http.post<Indicador>(`${this.base}/indicadores`, dto);
  }

  editarIndicador(inmCod: number, dto: Partial<Indicador>) {
    return this.http.put<Indicador>(`${this.base}/indicadores/${inmCod}`, dto);
  }

  alterarSituacaoIndicador(inmCod: number, situacao: EAtivoInativo) {
    return this.http.patch<Indicador>(`${this.base}/indicadores/${inmCod}/situacao`, null, {
      params: { situacao },
    });
  }
}
