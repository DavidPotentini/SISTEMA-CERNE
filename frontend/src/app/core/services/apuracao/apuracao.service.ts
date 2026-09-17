import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import {
  ApuracaoIndicador,
  PainelIndicador,
  PeriodoApuracao,
} from '../../../models/indicador/indicador.model';

@Injectable({ providedIn: 'root' })
export class ApuracaoService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/apuracao`;

  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  listar() {
    return this.http.get<ApuracaoIndicador[]>(this.base);
  }

  painel() {
    return this.http.get<PainelIndicador[]>(`${this.base}/painel`);
  }

  periodos(indCod: number) {
    return this.http.get<PeriodoApuracao[]>(`${this.base}/${indCod}/periodos`);
  }

  registrar(indCod: number, metCod: number, valor: number) {
    return this.http.put<PeriodoApuracao>(`${this.base}/${indCod}/periodos/${metCod}`, { valor });
  }
}
