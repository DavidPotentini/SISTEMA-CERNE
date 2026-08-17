import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Ciclo } from '../../../models/ciclo/ciclo.model';

/**
 * Ciclos da própria incubadora (o schema do tenant vem do JWT). Criar encerra o ativo anterior;
 * "pôr em foco" alterna o ciclo refletido nas telas. O encerramento terá tela própria no futuro.
 */
@Injectable({ providedIn: 'root' })
export class CicloService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/ciclos`;

  /** Incrementa a cada mutação; a lista observa para recarregar. */
  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  listar() {
    return this.http.get<Ciclo[]>(this.base);
  }

  criar(dto: Partial<Ciclo>) {
    return this.http.post<Ciclo>(this.base, dto);
  }

  porEmFoco(cicCod: number) {
    return this.http.patch<Ciclo>(`${this.base}/${cicCod}/foco`, {});
  }
}
