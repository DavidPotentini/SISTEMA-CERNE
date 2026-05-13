import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IncubadaRequest, IncubadaResponse } from '../../../models/incubadas/incubada.model';

@Injectable({ providedIn: 'root' })
export class IncubadasService {
  private base = 'http://localhost:8080/gerenciaIncubadas';

  constructor(private http: HttpClient) {}

  findAll(): Observable<IncubadaResponse[]> {
    return this.http.get<IncubadaResponse[]>(this.base);
  }
  findById(incCod: number): Observable<IncubadaResponse> {
    return this.http.get<IncubadaResponse>(`${this.base}/${incCod}`);
  }
  save(body: IncubadaRequest): Observable<IncubadaResponse> {
    return this.http.post<IncubadaResponse>(this.base, body);
  }
  update(incCod: number, body: IncubadaRequest): Observable<IncubadaResponse> {
    return this.http.put<IncubadaResponse>(`${this.base}/${incCod}`, body);
  }
  delete(incCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${incCod}`);
  }
}
