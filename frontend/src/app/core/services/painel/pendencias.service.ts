import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Pendencia } from '../../../models/painel/pendencias.model';

/**
 * Pendências da incubadora (tenant vem do JWT). Uma tela, um endpoint: a lista única de
 * pendências do ciclo ativo. Somente leitura — a tela recarrega via {@code reload()} do rxResource.
 */
@Injectable({ providedIn: 'root' })
export class PendenciasService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/pendencias`;

  pendencias() {
    return this.http.get<Pendencia[]>(this.base);
  }
}
