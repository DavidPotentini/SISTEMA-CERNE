import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import {
  ApuracaoIndicador,
  PainelIndicador,
  PeriodoApuracao,
} from '../../../models/indicador/indicador.model';

/**
 * Apuração de indicadores do ciclo ativo (tenant vem do JWT). Lista os indicadores com o resumo
 * apurados/total e registra o resultado de cada período (o backend carimba usuário e data).
 */
@Injectable({ providedIn: 'root' })
export class ApuracaoService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/apuracao`;

  /** Incrementa a cada registro; a listagem observa para recarregar o resumo. */
  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  /** Indicadores do ciclo com o resumo de apuração. */
  listar() {
    return this.http.get<ApuracaoIndicador[]>(this.base);
  }

  /** Painel do ciclo: uma linha por indicador com meta/resultado somados e atingido/pendente. */
  painel() {
    return this.http.get<PainelIndicador[]>(`${this.base}/painel`);
  }

  /** Períodos de um indicador com o resultado apurado (quando houver). */
  periodos(indCod: number) {
    return this.http.get<PeriodoApuracao[]>(`${this.base}/${indCod}/periodos`);
  }

  /** Registra (ou atualiza) o resultado de um período. */
  registrar(indCod: number, metCod: number, valor: number) {
    return this.http.put<PeriodoApuracao>(`${this.base}/${indCod}/periodos/${metCod}`, { valor });
  }
}
