import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Ciclo } from '../../../models/ciclo/ciclo.model';
import {
  Agrupamento,
  AtividadeMetodologia,
  EAtivoInativo,
  GerarCicloOpcoes,
  Indicador,
  Pratica,
  Processo,
} from '../../../models/metodologia/metodologia.model';

@Injectable({ providedIn: 'root' })
export class MetodologiaService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/metodologia`;
  private readonly materializacaoBase = `${environment.apiUrl}/incubadora/materializacao-metodologia`;

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

  reordenarProcessos(prcCods: number[]) {
    return this.http.put<Processo[]>(`${this.base}/processos/ordem`, prcCods);
  }

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

  reordenarPraticas(prcCod: number, prtCods: number[]) {
    return this.http.put<Processo[]>(`${this.base}/processos/${prcCod}/praticas/ordem`, prtCods);
  }

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

  listarAgrupamentos() {
    return this.http.get<Agrupamento[]>(`${this.base}/agrupamentos`);
  }

  adicionarAgrupamento(prtCod: number, dto: Partial<Agrupamento>) {
    return this.http.post<Agrupamento>(`${this.base}/praticas/${prtCod}/agrupamentos`, dto);
  }

  editarAgrupamento(agrCod: number, dto: Partial<Agrupamento>) {
    return this.http.put<Agrupamento>(`${this.base}/agrupamentos/${agrCod}`, dto);
  }

  reordenarAgrupamentos(prtCod: number, agrCods: number[]) {
    return this.http.put<Agrupamento[]>(`${this.base}/praticas/${prtCod}/agrupamentos/ordem`, agrCods);
  }

  alterarSituacaoAgrupamento(agrCod: number, situacao: EAtivoInativo) {
    return this.http.patch<Agrupamento>(`${this.base}/agrupamentos/${agrCod}/situacao`, null, {
      params: { situacao },
    });
  }

  excluirAgrupamento(agrCod: number) {
    return this.http.delete<void>(`${this.base}/agrupamentos/${agrCod}`);
  }

  listarAtividades() {
    return this.http.get<AtividadeMetodologia[]>(`${this.base}/atividades`);
  }

  criarAtividade(dto: Partial<AtividadeMetodologia>) {
    return this.http.post<AtividadeMetodologia>(`${this.base}/atividades`, dto);
  }

  editarAtividade(ameCod: number, dto: Partial<AtividadeMetodologia>) {
    return this.http.put<AtividadeMetodologia>(`${this.base}/atividades/${ameCod}`, dto);
  }

  reordenarAtividades(prtCod: number, ameCods: number[]) {
    return this.http.put<AtividadeMetodologia[]>(`${this.base}/praticas/${prtCod}/atividades/ordem`, ameCods);
  }

  alterarSituacaoAtividade(ameCod: number, situacao: EAtivoInativo) {
    return this.http.patch<AtividadeMetodologia>(`${this.base}/atividades/${ameCod}/situacao`, null, {
      params: { situacao },
    });
  }

  alterarPorEmpreendimentoAtividade(ameCod: number, valor: boolean) {
    return this.http.patch<AtividadeMetodologia>(
      `${this.base}/atividades/${ameCod}/por-empreendimento`,
      null,
      { params: { valor } },
    );
  }

  excluirAtividade(ameCod: number) {
    return this.http.delete<void>(`${this.base}/atividades/${ameCod}`);
  }

  alvoMaterializacao() {
    return this.http.get<Ciclo | null>(`${this.materializacaoBase}/alvo`);
  }

  opcoesGerar() {
    return this.http.get<GerarCicloOpcoes>(`${this.materializacaoBase}/empreendimentos`);
  }

  materializarMetodologia(empCods: number[]) {
    return this.http.post<Ciclo>(this.materializacaoBase, { empCods });
  }
}
