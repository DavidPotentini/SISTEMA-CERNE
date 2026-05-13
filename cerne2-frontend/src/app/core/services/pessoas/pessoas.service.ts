import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PessoaRequest, PessoaResponse } from '../../../models/pessoas/pessoa.model';

@Injectable({ providedIn: 'root' })
export class PessoasService {
  private base = 'http://localhost:8080/pessoas';

  constructor(private http: HttpClient) {}

  findByIncubadora(): Observable<PessoaResponse[]> {
    return this.http.get<PessoaResponse[]>(this.base);
  }
  findByIncubada(incCod: number): Observable<PessoaResponse[]> {
    return this.http.get<PessoaResponse[]>(`${this.base}/incubada/${incCod}`);
  }
  findById(pessoaCod: number): Observable<PessoaResponse> {
    return this.http.get<PessoaResponse>(`${this.base}/${pessoaCod}`);
  }
  save(body: PessoaRequest): Observable<PessoaResponse> {
    return this.http.post<PessoaResponse>(this.base, body);
  }
  update(pessoaCod: number, body: PessoaRequest): Observable<PessoaResponse> {
    return this.http.put<PessoaResponse>(`${this.base}/${pessoaCod}`, body);
  }
  delete(pessoaCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${pessoaCod}`);
  }
}
