import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ArquivoDTO } from '../../../models/arquivo/arquivo.model';

/**
 * Cliente genérico de anexos. Os endpoints de arquivo ficam aninhados nos
 * controllers de incubadas e evidências, então as URLs são passadas prontas
 * pelo componente que usa o serviço.
 *
 * - `arquivosUrl`     → `.../arquivos` (GET lista, POST upload)
 * - `arquivoBaseUrl`  → base de `.../arquivos/{arqCod}` (GET download, DELETE)
 */
@Injectable({ providedIn: 'root' })
export class ArquivoService {
  constructor(private http: HttpClient) {}

  listar(arquivosUrl: string): Observable<ArquivoDTO[]> {
    return this.http.get<ArquivoDTO[]>(arquivosUrl);
  }

  upload(arquivosUrl: string, arquivo: File): Observable<ArquivoDTO> {
    const formData = new FormData();
    formData.append('multipartFile', arquivo);
    return this.http.post<ArquivoDTO>(arquivosUrl, formData);
  }

  /** Devolve a URL temporária (pré-assinada) para baixar o arquivo. */
  urlDownload(arquivoBaseUrl: string, arqCod: number): Observable<string> {
    return this.http.get(`${arquivoBaseUrl}/${arqCod}`, { responseType: 'text' });
  }

  excluir(arquivoBaseUrl: string, arqCod: number): Observable<void> {
    return this.http.delete<void>(`${arquivoBaseUrl}/${arqCod}`);
  }
}
