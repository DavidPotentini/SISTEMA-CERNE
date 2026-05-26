// planejamento.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PlanejamentoDTO } from '../../../models/planejamento/planejamento.model';
import { ProjetoDTO } from '../../../models/planejamento/projeto.model';
import { ObjetivoDTO } from '../../../models/planejamento/objetivo.model';
import { TarefaDTO } from '../../../models/planejamento/tarefa.model';
import { EvidenciaDTO } from '../../../models/planejamento/evidencia.model';


@Injectable({ providedIn: 'root' })
export class PlanejamentoService {
  private host = 'http://localhost:8080';
  private base = `${this.host}/planejamento`;

  constructor(private http: HttpClient) {}

  private planejamentoUrl(incCod?: number | null): string {
    return incCod ? `${this.host}/${incCod}/planejamento` : this.base;
  }

  // Planos
  findAll(incCod?: number | null): Observable<PlanejamentoDTO[]> {
    return this.http.get<PlanejamentoDTO[]>(this.planejamentoUrl(incCod));
  }
  findById(pesCod: number, incCod?: number | null): Observable<PlanejamentoDTO> {
    return this.http.get<PlanejamentoDTO>(`${this.planejamentoUrl(incCod)}/${pesCod}`);
  }
  save(body: PlanejamentoDTO, incCod?: number | null): Observable<PlanejamentoDTO> {
    return this.http.post<PlanejamentoDTO>(this.planejamentoUrl(incCod), body);
  }
  update(pesCod: number, body: PlanejamentoDTO, incCod?: number | null): Observable<PlanejamentoDTO> {
    return this.http.put<PlanejamentoDTO>(`${this.planejamentoUrl(incCod)}/${pesCod}`, body);
  }
  delete(pesCod: number, incCod?: number | null): Observable<void> {
    return this.http.delete<void>(`${this.planejamentoUrl(incCod)}/${pesCod}`);
  }

  // Projetos
  findProjetos(pesCod: number): Observable<ProjetoDTO[]> {
    return this.http.get<ProjetoDTO[]>(`${this.base}/${pesCod}/projetos`);
  }
  findByProjetoId(pesCod: number, prjCod:number): Observable<ProjetoDTO> {
    return this.http.get<ProjetoDTO>(`${this.base}/${pesCod}/projetos/${prjCod}`);
  }
  saveProjeto(pesCod: number, body: ProjetoDTO): Observable<ProjetoDTO> {
    return this.http.post<ProjetoDTO>(`${this.base}/${pesCod}/projetos`, body);
  }
  updateProjeto(pesCod: number, prjCod: number, body: ProjetoDTO): Observable<ProjetoDTO> {
    return this.http.put<ProjetoDTO>(`${this.base}/${pesCod}/projetos/${prjCod}`, body);
  }
  deleteProjeto(pesCod: number, prjCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${pesCod}/projetos/${prjCod}`);
  }

  // Objetivos
  findObjetivos(pesCod: number, prjCod: number): Observable<ObjetivoDTO[]> {
    return this.http.get<ObjetivoDTO[]>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos`);
  }
  findByObjetivoId(pesCod: number, prjCod: number, objCod:number): Observable<ObjetivoDTO>{
    return this.http.get<ObjetivoDTO>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}`);
  }
  saveObjetivo(pesCod: number, prjCod: number, body: ObjetivoDTO): Observable<ObjetivoDTO> {
    return this.http.post<ObjetivoDTO>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos`, body);
  }
  updateObjetivo(pesCod: number, prjCod: number, objCod: number, body: ObjetivoDTO):Observable<ObjetivoDTO>{
    return this.http.put<ObjetivoDTO>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}`, body);
  }
  deleteObjetivo(pesCod: number, prjCod: number, objCod: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}`);
  }

  // Tarefas
  findTarefas(pesCod: number, prjCod: number, objCod: number): Observable<TarefaDTO[]> {
    return this.http.get<TarefaDTO[]>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}/tarefas`);
  }
  findByTarefaId(pesCod: number, prjCod: number, objCod: number, trfCod: number): Observable<TarefaDTO> {
    return this.http.get<TarefaDTO>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}/tarefas/${trfCod}`);
  }
  saveTarefa(pesCod: number, prjCod: number, objCod: number, body: TarefaDTO): Observable<TarefaDTO> {
    return this.http.post<TarefaDTO>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}/tarefas`, body);
  }
  updateTarefa(pesCod: number, prjCod: number, objCod: number,trfCod:number, body: TarefaDTO): Observable<TarefaDTO> {
    return this.http.put<TarefaDTO>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}/tarefas/${trfCod}`, body);
  }
  deleteTarefa(pesCod: number, prjCod: number, objCod: number,trfCod:number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}/tarefas/${trfCod}`);
  }
  avaliarTarefa(
    incCod: number,
    pesCod: number,
    prjCod: number,
    objCod: number,
    trfCod: number,
    body: { pontuacao: number | null; observacao: string | null },
  ): Observable<TarefaDTO> {
    return this.http.patch<TarefaDTO>(
      `${this.host}/${incCod}/planejamento/${pesCod}/projetos/${prjCod}/objetivos/${objCod}/tarefas/${trfCod}/avaliacao`,
      body,
    );
  }

  // Evidências
  private evidenciasBase(pesCod: number, prjCod: number, objCod: number, trfCod: number): string {
    return `${this.base}/${pesCod}/projetos/${prjCod}/objetivos/${objCod}/tarefas/${trfCod}/evidencias`;
  }
  findEvidencias(pesCod: number, prjCod: number, objCod: number, trfCod: number): Observable<EvidenciaDTO[]> {
    return this.http.get<EvidenciaDTO[]>(this.evidenciasBase(pesCod, prjCod, objCod, trfCod));
  }
  saveEvidencia(pesCod: number, prjCod: number, objCod: number, trfCod: number, body: EvidenciaDTO): Observable<EvidenciaDTO> {
    return this.http.post<EvidenciaDTO>(this.evidenciasBase(pesCod, prjCod, objCod, trfCod), body);
  }
  updateEvidencia(pesCod: number, prjCod: number, objCod: number, trfCod: number, evdCod: number, body: EvidenciaDTO): Observable<EvidenciaDTO> {
    return this.http.post<EvidenciaDTO>(`${this.evidenciasBase(pesCod, prjCod, objCod, trfCod)}/${evdCod}`, body);
  }
  deleteEvidencia(pesCod: number, prjCod: number, objCod: number, trfCod: number, evdCod: number): Observable<void> {
    return this.http.delete<void>(`${this.evidenciasBase(pesCod, prjCod, objCod, trfCod)}/${evdCod}`);
  }
}