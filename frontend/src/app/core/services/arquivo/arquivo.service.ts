import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Arquivo } from '../../../models/evidencia/evidencia.model';

@Injectable({ providedIn: 'root' })
export class ArquivoService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/arquivos`;

  upload(arquivo: File) {
    const form = new FormData();
    form.append('arquivo', arquivo);
    return this.http.post<Arquivo>(this.base, form);
  }

  buscar(arqCod: number) {
    return this.http.get<Arquivo>(`${this.base}/${arqCod}`);
  }
}
