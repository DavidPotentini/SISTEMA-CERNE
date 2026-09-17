import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { EStatusAtividade } from '../../../models/planejamento/planejamento.model';

@Injectable({ providedIn: 'root' })
export class ExecucaoService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/execucao`;

  mudarStatus(atpCod: number, status: EStatusAtividade) {
    return this.http.put<void>(`${this.base}/atividades/${atpCod}/status`, { status });
  }
}
