import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Aplicacao, Rodada } from '../../../models/monitoramento/monitoramento.model';

/**
 * Monitoramento das incubadas (tenant vem do JWT). Rodadas (planejar/listar/concluir) e aplicações
 * por rodada (cards + revisão). O `versao` é o gatilho: um `recarregar()` após qualquer mutação
 * refaz a listagem de rodadas e as aplicações abertas que observem o mesmo sinal.
 */
@Injectable({ providedIn: 'root' })
export class MonitoramentoService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/monitoramento`;

  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  listarRodadas() {
    return this.http.get<Rodada[]>(`${this.base}/rodadas`);
  }

  planejar(dto: Partial<Rodada>) {
    return this.http.post<Rodada>(`${this.base}/rodadas`, dto);
  }

  concluir(rodCod: number) {
    return this.http.patch<Rodada>(`${this.base}/rodadas/${rodCod}/concluir`, {});
  }

  aplicacoes(rodCod: number) {
    return this.http.get<Aplicacao[]>(`${this.base}/rodadas/${rodCod}/aplicacoes`);
  }

  revisar(rodCod: number, empCod: number, dto: Partial<Aplicacao>) {
    return this.http.put<Aplicacao>(
      `${this.base}/rodadas/${rodCod}/empreendimentos/${empCod}/avaliacao`,
      dto,
    );
  }
}
