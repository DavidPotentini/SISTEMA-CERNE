import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Ciclo } from '../../../models/ciclo/ciclo.model';

@Injectable({ providedIn: 'root' })
export class CicloService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/ciclos`;

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

  encerrar(cicCod: number) {
    return this.http.patch<Ciclo>(`${this.base}/${cicCod}/encerramento`, {});
  }
}
