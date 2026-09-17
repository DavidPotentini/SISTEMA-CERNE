import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { IncubadoraDetalhe, IncubadoraResumo } from '../../../models/incubadora/incubadora.model';

@Injectable({ providedIn: 'root' })
export class IncubadoraService {
  private readonly http = inject(HttpClient);
  readonly base = `${environment.apiUrl}/admin/incubadoras`;

  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  listar(nome?: string, status?: string) {
    let params = new HttpParams();
    if (nome) params = params.set('nome', nome);
    if (status) params = params.set('status', status);
    return this.http.get<IncubadoraResumo[]>(this.base, { params });
  }

  buscar(id: number) {
    return this.http.get<IncubadoraDetalhe>(`${this.base}/${id}`);
  }

  criar(dto: IncubadoraDetalhe) {
    return this.http.post<IncubadoraDetalhe>(this.base, dto);
  }

  salvar(id: number, dto: IncubadoraDetalhe) {
    return this.http.put<IncubadoraDetalhe>(`${this.base}/${id}`, dto);
  }

  alternarStatus(id: number) {
    return this.http.patch<IncubadoraDetalhe>(`${this.base}/${id}/status`, {});
  }
}
