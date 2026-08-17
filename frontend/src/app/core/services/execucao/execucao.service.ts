import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { EStatusAtividade } from '../../../models/planejamento/planejamento.model';

/**
 * Acompanhamento de execução (tenant vem do JWT). A leitura do plano/estrutura reaproveita o
 * `PlanejamentoService`; aqui só existe a mutação de status da atividade. Concluir exige que todas as
 * evidências da atividade estejam validadas (regra no backend).
 */
@Injectable({ providedIn: 'root' })
export class ExecucaoService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/execucao`;

  /** Muda o estado de execução da atividade (planejada, em andamento ou concluída). */
  mudarStatus(atpCod: number, status: EStatusAtividade) {
    return this.http.put<void>(`${this.base}/atividades/${atpCod}/status`, { status });
  }
}
