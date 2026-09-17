import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { ResumoCiclo } from '../../../models/painel/visao-geral.model';

@Injectable({ providedIn: 'root' })
export class VisaoGeralService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/painel-visao-geral`;

  resumo() {
    return this.http.get<ResumoCiclo>(this.base);
  }
}
