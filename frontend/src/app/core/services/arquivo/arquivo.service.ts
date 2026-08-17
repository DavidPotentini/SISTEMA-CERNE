import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Arquivo } from '../../../models/evidencia/evidencia.model';

/**
 * Upload e leitura de arquivos (tenant vem do JWT). Reutilizável: sobe o binário, recebe o `arqCod` e
 * passa esse código nas entidades que anexam arquivo (ex.: evidências).
 */
@Injectable({ providedIn: 'root' })
export class ArquivoService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/arquivos`;

  /** Sobe um arquivo (multipart) e devolve metadados + URL de download temporária. */
  upload(arquivo: File) {
    const form = new FormData();
    form.append('arquivo', arquivo);
    return this.http.post<Arquivo>(this.base, form);
  }

  /** Metadados + URL de download temporária (presigned) de um arquivo. */
  buscar(arqCod: number) {
    return this.http.get<Arquivo>(`${this.base}/${arqCod}`);
  }
}
