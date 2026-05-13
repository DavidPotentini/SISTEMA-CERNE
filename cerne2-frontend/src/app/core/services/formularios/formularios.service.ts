import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FormularioDTO } from '../../../models/formularios/formulario.model';

@Injectable({ providedIn: 'root' })
export class FormulariosService {
  private base = 'http://localhost:8080/formularios';

  constructor(private http: HttpClient) {}

  findAll(): Observable<FormularioDTO[]> {
    return this.http.get<FormularioDTO[]>(this.base);
  }

  findByFrmCod(frmCod: number): Observable<FormularioDTO> {
    return this.http.get<FormularioDTO>(`${this.base}/${frmCod}`);
  }

  save(body: FormularioDTO): Observable<FormularioDTO> {
    return this.http.post<FormularioDTO>(this.base, body);
  }

  update(frmCod: number, body: FormularioDTO): Observable<FormularioDTO> {
    return this.http.put<FormularioDTO>(`${this.base}/${frmCod}`, body);
  }

  delete(frmCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${frmCod}`);
  }
}
