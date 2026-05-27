import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  HipoteseDTO,
  QuadroValidacaoHipoteseDTO,
} from '../../../models/validacao-hipotese/validacao-hipotese.model';

@Injectable({ providedIn: 'root' })
export class ValidacaoHipoteseService {
  private readonly host = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  private base(incCod: number): string {
    return `${this.host}/incubadas/${incCod}/validacaoHipotese`;
  }

  findAllByIncCod(incCod: number): Observable<QuadroValidacaoHipoteseDTO[]> {
    return this.http.get<QuadroValidacaoHipoteseDTO[]>(this.base(incCod));
  }

  findById(incCod: number, qvhCod: number): Observable<QuadroValidacaoHipoteseDTO> {
    return this.http.get<QuadroValidacaoHipoteseDTO>(`${this.base(incCod)}/${qvhCod}`);
  }

  save(
    incCod: number,
    body: QuadroValidacaoHipoteseDTO,
  ): Observable<QuadroValidacaoHipoteseDTO> {
    return this.http.post<QuadroValidacaoHipoteseDTO>(this.base(incCod), body);
  }

  update(
    incCod: number,
    qvhCod: number,
    body: QuadroValidacaoHipoteseDTO,
  ): Observable<QuadroValidacaoHipoteseDTO> {
    return this.http.put<QuadroValidacaoHipoteseDTO>(`${this.base(incCod)}/${qvhCod}`, body);
  }

  delete(incCod: number, qvhCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base(incCod)}/${qvhCod}`);
  }

  findHipoteses(incCod: number, qvhCod: number): Observable<HipoteseDTO[]> {
    return this.http.get<HipoteseDTO[]>(`${this.base(incCod)}/${qvhCod}/hipoteses`);
  }

  saveHipotese(
    incCod: number,
    qvhCod: number,
    hipCod: number | null,
    body: HipoteseDTO,
  ): Observable<HipoteseDTO> {
    const url = `${this.base(incCod)}/${qvhCod}/hipoteses`;
    return hipCod == null
      ? this.http.post<HipoteseDTO>(url, body)
      : this.http.put<HipoteseDTO>(`${url}/${hipCod}`, body);
  }

  deleteHipotese(incCod: number, qvhCod: number, hipCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base(incCod)}/${qvhCod}/hipoteses/${hipCod}`);
  }
}
