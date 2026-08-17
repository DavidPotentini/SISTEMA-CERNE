import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { IncubadoraDetalhe } from '../../../models/incubadora/incubadora.model';

/** Dados da incubadora do usuário logado (não admin; o schema do tenant vem do JWT). */
@Injectable({ providedIn: 'root' })
export class MinhaIncubadoraService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora`;

  buscar() {
    return this.http.get<IncubadoraDetalhe>(`${this.base}/minha`);
  }
}
