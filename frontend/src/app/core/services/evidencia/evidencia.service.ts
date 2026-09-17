import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import {
  AtividadeOpcao,
  EStatusEvidencia,
  Evidencia,
} from '../../../models/evidencia/evidencia.model';

@Injectable({ providedIn: 'root' })
export class EvidenciaService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/evidencias`;

  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  listar() {
    return this.http.get<Evidencia[]>(this.base);
  }

  atividades() {
    return this.http.get<AtividadeOpcao[]>(`${this.base}/atividades`);
  }

  historico(evdCod: number) {
    return this.http.get<Evidencia[]>(`${this.base}/${evdCod}`);
  }

  registrar(dto: Partial<Evidencia>) {
    return this.http.post<Evidencia>(this.base, dto);
  }

  corrigir(evdCod: number, dto: Partial<Evidencia>) {
    return this.http.post<Evidencia>(`${this.base}/${evdCod}/correcoes`, dto);
  }

  avaliar(evdCod: number, status: EStatusEvidencia, motivo: string | null) {
    return this.http.post<Evidencia>(`${this.base}/${evdCod}/avaliacoes`, { status, motivo });
  }
}
