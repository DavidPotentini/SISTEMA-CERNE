
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ServicoValorAgregadoRequest, ServicoValorAgregadoResponse } from '../../../models/servico-valor-agregado/servico-valor-agregado.model';


@Injectable({ providedIn: 'root' })
export class ServicoValorAgregadoService {
  private base = 'http://localhost:8080/servicosValorAgregado';

  constructor(private http: HttpClient) {}

  findAll(): Observable<ServicoValorAgregadoResponse[]> {
    return this.http.get<ServicoValorAgregadoResponse[]>(this.base);
  }
  findById(servCod: number): Observable<ServicoValorAgregadoResponse> {
    return this.http.get<ServicoValorAgregadoResponse>(`${this.base}/${servCod}`);
  }
  save(body: ServicoValorAgregadoRequest): Observable<ServicoValorAgregadoResponse> {
    return this.http.post<ServicoValorAgregadoResponse>(this.base, body);
  }
  update(servCod: number, body: ServicoValorAgregadoRequest): Observable<ServicoValorAgregadoResponse> {
    return this.http.put<ServicoValorAgregadoResponse>(`${this.base}/${servCod}`, body);
  }
  delete(servCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${servCod}`);
  }
  }