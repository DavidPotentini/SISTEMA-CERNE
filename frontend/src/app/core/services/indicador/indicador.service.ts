import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { IndicadorCiclo, Meta, PraticaOpcao } from '../../../models/indicador/indicador.model';

@Injectable({ providedIn: 'root' })
export class IndicadorService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/indicadores`;

  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  listar() {
    return this.http.get<IndicadorCiclo[]>(this.base);
  }

  vinculos() {
    return this.http.get<PraticaOpcao[]>(`${this.base}/vinculos`);
  }

  definirComplementar(dto: Partial<IndicadorCiclo>) {
    return this.http.post<IndicadorCiclo>(`${this.base}/complementares`, dto);
  }

  editar(indCod: number, dto: Partial<IndicadorCiclo>) {
    return this.http.put<IndicadorCiclo>(`${this.base}/${indCod}`, dto);
  }

  listarMetas(indCod: number) {
    return this.http.get<Meta[]>(`${this.base}/${indCod}/metas`);
  }

  criarMeta(indCod: number, dto: Partial<Meta>) {
    return this.http.post<Meta>(`${this.base}/${indCod}/metas`, dto);
  }

  editarMeta(indCod: number, metCod: number, dto: Partial<Meta>) {
    return this.http.put<Meta>(`${this.base}/${indCod}/metas/${metCod}`, dto);
  }

  removerMeta(indCod: number, metCod: number) {
    return this.http.delete<void>(`${this.base}/${indCod}/metas/${metCod}`);
  }
}
