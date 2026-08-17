import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { PapelResumo, UsuarioResumo } from '../../../models/usuario/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http = inject(HttpClient);
  readonly base = `${environment.apiUrl}/admin/usuarios`;

  /** Incrementa a cada mutação; a lista observa para recarregar. */
  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  listar() {
    return this.http.get<UsuarioResumo[]>(this.base);
  }

  /** Papéis disponíveis para atribuição na incubadora (vazio se não provisionada). */
  listarPapeis(incCod: number) {
    const params = new HttpParams().set('incCod', incCod);
    return this.http.get<PapelResumo[]>(`${this.base}/papeis`, { params });
  }

  convidar(dto: { nome: string; email: string; incCod: number | null; papCod: number | null }) {
    return this.http.post<UsuarioResumo>(this.base, dto);
  }

  editar(id: number, dto: { nome: string; incCod: number | null; papCod: number | null }) {
    return this.http.put<UsuarioResumo>(`${this.base}/${id}`, dto);
  }

  alternarStatus(id: number) {
    return this.http.patch<UsuarioResumo>(`${this.base}/${id}/status`, {});
  }
}
