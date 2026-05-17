import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IncubadaDTO } from '../../../models/incubadas/incubada.model';

@Injectable({ providedIn: 'root' })
export class IncubadasService {
  private base = 'http://localhost:8080/gerenciaIncubadas';

  constructor(private http: HttpClient) {}

  findAll(): Observable<IncubadaDTO[]> {
    return this.http.get<IncubadaDTO[]>(this.base);
  }
  findById(incCod: number): Observable<IncubadaDTO> {
    return this.http.get<IncubadaDTO>(`${this.base}/${incCod}`);
  }
  save(body: IncubadaDTO): Observable<IncubadaDTO> {
    return this.http.post<IncubadaDTO>(this.base, body);
  }
  update(incCod: number, body: IncubadaDTO): Observable<IncubadaDTO> {
    return this.http.put<IncubadaDTO>(`${this.base}/${incCod}`, body);
  }
  delete(incCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${incCod}`);
  }
}
