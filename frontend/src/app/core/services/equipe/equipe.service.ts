import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { PessoaEquipe } from '../../../models/equipe/equipe.model';

/** Equipe vinculada da própria incubadora (o schema do tenant vem do JWT). */
@Injectable({ providedIn: 'root' })
export class EquipeService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/equipe`;

  listar() {
    return this.http.get<PessoaEquipe[]>(this.base);
  }
}
