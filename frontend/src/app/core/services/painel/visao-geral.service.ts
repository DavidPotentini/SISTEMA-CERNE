import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { ResumoCiclo } from '../../../models/painel/visao-geral.model';

/**
 * Painel de visão geral da incubadora (tenant vem do JWT). Uma tela, um endpoint: o resumo agregado
 * do andamento do ciclo em foco. Somente leitura — a tela carrega via rxResource.
 */
@Injectable({ providedIn: 'root' })
export class VisaoGeralService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/painel-visao-geral`;

  resumo() {
    return this.http.get<ResumoCiclo>(this.base);
  }
}
