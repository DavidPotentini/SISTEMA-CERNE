import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import {
  AtividadePlanejada,
  PlanejamentoAtual,
  PlanProcesso,
} from '../../../models/planejamento/planejamento.model';

@Injectable({ providedIn: 'root' })
export class PlanejamentoService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/planejamento`;

  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  atual() {
    return this.http.get<PlanejamentoAtual>(this.base);
  }

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

  reordenarAtividades(prtcCod: number, atpCods: number[]) {
    return this.http.put<void>(`${this.base}/praticas/${prtcCod}/atividades/ordem`, atpCods);
  }

  reordenarAgrupamentos(prtcCod: number, agrcCods: number[]) {
    return this.http.put<void>(`${this.base}/praticas/${prtcCod}/agrupamentos/ordem`, agrcCods);
  }

  excluirAgrupamento(agrcCod: number) {
    return this.http.delete<void>(`${this.base}/agrupamentos/${agrcCod}`);
  }

  gerarAtividadesEmpreendimento(empCod: number) {
    return this.http.post<void>(`${this.base}/empreendimentos/${empCod}/atividades`, {});
  }
}
