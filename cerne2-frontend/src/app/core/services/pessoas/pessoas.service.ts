import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PessoaDTO } from '../../../models/pessoas/pessoa.model';

@Injectable({ providedIn: 'root' })
export class PessoasService {
  private base = 'http://localhost:8080/pessoas';

  constructor(private http: HttpClient) {}

  findByIncubadora(): Observable<PessoaDTO[]> {
    return this.http.get<PessoaDTO[]>(this.base);
  }
  findByIncubada(incCod: number): Observable<PessoaDTO[]> {
    return this.http.get<PessoaDTO[]>(`${this.base}/incubada/${incCod}`);
  }
  findById(pessoaCod: number): Observable<PessoaDTO> {
    return this.http.get<PessoaDTO>(`${this.base}/${pessoaCod}`);
  }
  save(body: PessoaDTO): Observable<PessoaDTO> {
    return this.http.post<PessoaDTO>(this.base, body);
  }
  update(pessoaCod: number, body: PessoaDTO): Observable<PessoaDTO> {
    return this.http.put<PessoaDTO>(`${this.base}/${pessoaCod}`, body);
  }
  delete(pessoaCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${pessoaCod}`);
  }
}
