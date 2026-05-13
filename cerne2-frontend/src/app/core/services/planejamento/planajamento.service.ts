// planejamento.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PlanejamentoResponse } from '../../../models/planejamento/planejamento.model';
import { PlanejamentoRequest } from '../../../models/planejamento/planejamento.model';
import { ProjetoResponse } from '../../../models/planejamento/projeto.model';
import { ProjetoRequest } from '../../../models/planejamento/projeto.model';
import { ObjetivoResponse } from '../../../models/planejamento/objetivo.model';
import { ObjetivoRequest } from '../../../models/planejamento/objetivo.model';
import { TarefaResponse } from '../../../models/planejamento/tarefa.model';
import { TarefaRequest } from '../../../models/planejamento/tarefa.model';
import { EvidenciaRequest, EvidenciaResponse } from '../../../models/planejamento/evidencia.model';


@Injectable({ providedIn: 'root' })
export class PlanejamentoService {
  private base = 'http://localhost:8080/planosAnuaisIntegrados';

  constructor(private http: HttpClient) {}

  // Planos
  findAll(): Observable<PlanejamentoResponse[]> {
    return this.http.get<PlanejamentoResponse[]>(this.base);
  }
  findById(pesCod: number): Observable<PlanejamentoResponse> {
    return this.http.get<PlanejamentoResponse>(`${this.base}/${pesCod}`);
  }
  save(body: PlanejamentoRequest): Observable<PlanejamentoResponse> {
    return this.http.post<PlanejamentoResponse>(this.base, body);
  }
  update(pesCod: number, body: PlanejamentoRequest): Observable<PlanejamentoResponse> {
    return this.http.put<PlanejamentoResponse>(`${this.base}/${pesCod}`, body);
  }
  delete(pesCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${pesCod}`);
  }

  // Projetos
  findProjetos(pesCod: number): Observable<ProjetoResponse[]> {
    return this.http.get<ProjetoResponse[]>(`${this.base}/${pesCod}/projetos`);
  }
  findByProjetoId(pesCod: number, prjCod:number): Observable<ProjetoResponse> {
    return this.http.get<ProjetoResponse>(`${this.base}/${pesCod}/projetos/${prjCod}`);
  }
  saveProjeto(pesCod: number, body: ProjetoRequest): Observable<ProjetoResponse> {
    return this.http.post<ProjetoResponse>(`${this.base}/${pesCod}/projetos`, body);
  }
  updateProjeto(pesCod: number, prjCod: number, body: ProjetoRequest): Observable<ProjetoResponse> {
    return this.http.put<ProjetoResponse>(`${this.base}/${pesCod}/projetos/${prjCod}`, body);
  }
  deleteProjeto(pesCod: number, prjCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${pesCod}/projetos/${prjCod}`);
  }

  // Objetivos
  findObjetivos(pesCod: number, prjCod: number): Observable<ObjetivoResponse[]> {
    return this.http.get<ObjetivoResponse[]>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos`);
  }
  findByObjetivoId(pesCod: number, prjCod: number, objCod:number): Observable<ObjetivoResponse>{
    return this.http.get<ObjetivoResponse>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}`);
  }
  saveObjetivo(pesCod: number, prjCod: number, body: ObjetivoRequest): Observable<ObjetivoResponse> {
    return this.http.post<ObjetivoResponse>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos`, body);
  }
  updateObjetivo(pesCod: number, prjCod: number, objCod: number, body: ObjetivoRequest):Observable<ObjetivoResponse>{
    return this.http.put<ObjetivoResponse>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}`, body);
  }
  deleteObjetivo(pesCod: number, prjCod: number, objCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}`);
  }

  // Tarefas
  findTarefas(pesCod: number, prjCod: number, objCod: number): Observable<TarefaResponse[]> {
    return this.http.get<TarefaResponse[]>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}/tarefas`);
  }
  findByTarefaId(pesCod: number, prjCod: number, objCod: number, trfCod: number): Observable<TarefaResponse> {
    return this.http.get<TarefaResponse>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}/tarefas/${trfCod}`);
  }
  saveTarefa(pesCod: number, prjCod: number, objCod: number, body: TarefaRequest): Observable<TarefaResponse> {
    return this.http.post<TarefaResponse>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}/tarefas`, body);
  }
  updateTarefa(pesCod: number, prjCod: number, objCod: number,trfCod:number, body: TarefaRequest): Observable<TarefaResponse> {
    return this.http.put<TarefaResponse>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}/tarefas/${trfCod}`, body);
  }
  deleteTarefa(pesCod: number, prjCod: number, objCod: number,trfCod:number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}/tarefas/${trfCod}`);
  }

  // Evidências
  private evidenciasBase(pesCod: number, prjCod: number, objCod: number, trfCod: number): string {
    return `${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}/tarefas/${trfCod}/evidencias`;
  }
  findEvidencias(pesCod: number, prjCod: number, objCod: number, trfCod: number): Observable<EvidenciaResponse[]> {
    return this.http.get<EvidenciaResponse[]>(this.evidenciasBase(pesCod, prjCod, objCod, trfCod));
  }
  saveEvidencia(pesCod: number, prjCod: number, objCod: number, trfCod: number, body: EvidenciaRequest): Observable<EvidenciaResponse> {
    return this.http.post<EvidenciaResponse>(this.evidenciasBase(pesCod, prjCod, objCod, trfCod), body);
  }
  updateEvidencia(pesCod: number, prjCod: number, objCod: number, trfCod: number, evdCod: number, body: EvidenciaRequest): Observable<EvidenciaResponse> {
    return this.http.post<EvidenciaResponse>(`${this.evidenciasBase(pesCod, prjCod, objCod, trfCod)}/${evdCod}`, body);
  }
  deleteEvidencia(pesCod: number, prjCod: number, objCod: number, trfCod: number, evdCod: number): Observable<void> {
    return this.http.delete<void>(`${this.evidenciasBase(pesCod, prjCod, objCod, trfCod)}/${evdCod}`);
  }
}