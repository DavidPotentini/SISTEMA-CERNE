import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Pendencia } from '../../../models/painel/pendencias.model';

@Injectable({ providedIn: 'root' })
export class PendenciasService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/pendencias`;

  pendencias() {
    return this.http.get<Pendencia[]>(this.base);
  }
}
