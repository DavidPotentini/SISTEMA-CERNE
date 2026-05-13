import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MetricaDTO } from '../../../models/metricas/metrica.model';

@Injectable({ providedIn: 'root' })
export class MetricasService {
  private base = 'http://localhost:8080/metricas';

  constructor(private http: HttpClient) {}

  findAll(): Observable<MetricaDTO[]> {
    return this.http.get<MetricaDTO[]>(this.base);
  }

  findByMetCod(metCod: number): Observable<MetricaDTO> {
    return this.http.get<MetricaDTO>(`${this.base}/${metCod}`);
  }

  save(body: MetricaDTO): Observable<MetricaDTO> {
    return this.http.post<MetricaDTO>(this.base, body);
  }

  update(metCod: number, body: MetricaDTO): Observable<MetricaDTO> {
    return this.http.put<MetricaDTO>(`${this.base}/${metCod}`, body);
  }

  delete(metCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${metCod}`);
  }
}
