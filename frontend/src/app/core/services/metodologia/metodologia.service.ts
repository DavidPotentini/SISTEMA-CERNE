import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import {
  EAtivoInativo,
  Indicador,
  Pratica,
  Processo,
  Versao,
} from '../../../models/metodologia/metodologia.model';

/**
 * Metodologia da própria incubadora (schema do tenant vem do JWT). O front busca a versão vigente e
 * envia o `verCod` ao listar/criar processos. Aba "Processos e Práticas".
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

  /** Versão de trabalho (RASCUNHO) — a que as abas editam. */
  versaoDeTrabalho() {
    return this.http.get<Versao>(`${this.base}/versoes/trabalho`);
  }

  /** Histórico de publicações (VIGENTE + HISTORICA), mais recentes primeiro. */
  listarVersoes() {
    return this.http.get<Versao[]>(`${this.base}/versoes`);
  }

  /** Publica o rascunho como nova versão (só se houver alterações pendentes). */
  publicar() {
    return this.http.post<Versao>(`${this.base}/versoes/publicar`, null);
  }

  listarProcessos(verCod: number) {
    return this.http.get<Processo[]>(`${this.base}/processos`, { params: { verCod } });
  }

  criarProcesso(dto: Partial<Processo>) {
    return this.http.post<Processo>(`${this.base}/processos`, dto);
  }

  editarProcesso(prcCod: number, dto: Partial<Processo>) {
    return this.http.put<Processo>(`${this.base}/processos/${prcCod}`, dto);
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

  listarIndicadores(verCod: number) {
    return this.http.get<Indicador[]>(`${this.base}/indicadores`, { params: { verCod } });
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
