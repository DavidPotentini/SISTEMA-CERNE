
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ServicoValorAgregadoDTO } from '../../../models/servico-valor-agregado/servico-valor-agregado.model';


@Injectable({ providedIn: 'root' })
export class ServicoValorAgregadoService {
  private base = 'http://localhost:8080/servicosValorAgregado';

  constructor(private http: HttpClient) {}

  findAll(): Observable<ServicoValorAgregadoDTO[]> {
    return this.http.get<ServicoValorAgregadoDTO[]>(this.base);
  }
  findById(servCod: number): Observable<ServicoValorAgregadoDTO> {
    return this.http.get<ServicoValorAgregadoDTO>(`${this.base}/${servCod}`);
  }
  save(body: ServicoValorAgregadoDTO): Observable<ServicoValorAgregadoDTO> {
    return this.http.post<ServicoValorAgregadoDTO>(this.base, body);
  }
  update(servCod: number, body: ServicoValorAgregadoDTO): Observable<ServicoValorAgregadoDTO> {
    return this.http.put<ServicoValorAgregadoDTO>(`${this.base}/${servCod}`, body);
  }
  delete(servCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${servCod}`);
  }
  }