import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { IndicadorCiclo, Meta, PraticaOpcao } from '../../../models/indicador/indicador.model';

/**
 * Indicadores do ciclo ativo (tenant vem do JWT). "Gerar" copia os indicadores da metodologia
 * vigente (substitui os gerados, mantém complementares); "Definir complementar" inclui um manual.
 */
@Injectable({ providedIn: 'root' })
export class IndicadorService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/indicadores`;

  /** Incrementa a cada mutação; a listagem observa para recarregar. */
  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  /** Indicadores do ciclo ativo. */
  listar() {
    return this.http.get<IndicadorCiclo[]>(this.base);
  }

  /** Práticas da metodologia vigente para o seletor de vínculo (cascata processo → prática). */
  vinculos() {
    return this.http.get<PraticaOpcao[]>(`${this.base}/vinculos`);
  }

  /** Gera (ou regenera) os indicadores do ciclo a partir da metodologia vigente. */
  gerar() {
    return this.http.post<IndicadorCiclo[]>(`${this.base}/gerar`, null);
  }

  /** Define um indicador complementar no ciclo ativo. */
  definirComplementar(dto: Partial<IndicadorCiclo>) {
    return this.http.post<IndicadorCiclo>(`${this.base}/complementares`, dto);
  }

  /** Define (ou desvincula, com `respPesCod` null) o responsável pela apuração do indicador. */
  definirResponsavel(indCod: number, respPesCod: number | null) {
    const params: Record<string, number> = respPesCod == null ? {} : { respPesCod };
    return this.http.put<IndicadorCiclo>(`${this.base}/${indCod}/responsavel`, null, { params });
  }

  // ---- metas (períodos) de um indicador ----

  /** Períodos (metas) de um indicador, em ordem do início de apuração. */
  listarMetas(indCod: number) {
    return this.http.get<Meta[]>(`${this.base}/${indCod}/metas`);
  }

  /** Cadastra um período (meta) do indicador. */
  criarMeta(indCod: number, dto: Partial<Meta>) {
    return this.http.post<Meta>(`${this.base}/${indCod}/metas`, dto);
  }

  /** Edita um período do indicador. */
  editarMeta(indCod: number, metCod: number, dto: Partial<Meta>) {
    return this.http.put<Meta>(`${this.base}/${indCod}/metas/${metCod}`, dto);
  }

  /** Remove um período do indicador. */
  removerMeta(indCod: number, metCod: number) {
    return this.http.delete<void>(`${this.base}/${indCod}/metas/${metCod}`);
  }
}
