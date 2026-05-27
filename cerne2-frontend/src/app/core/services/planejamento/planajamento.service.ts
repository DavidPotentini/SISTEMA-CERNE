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
    return incCod ? `${this.host}/incubadas/${incCod}/planejamento` : this.base;
  }

  private projetosUrl(pesCod: number, incCod?: number | null): string {
    console.log(`${this.planejamentoUrl(incCod)}/${pesCod}/projetos`);
    return `${this.planejamentoUrl(incCod)}/${pesCod}/projetos`;
  }

  private objetivosUrl(pesCod: number, prjCod: number, incCod?: number | null): string {
    return `${this.projetosUrl(pesCod, incCod)}/${prjCod}/objetivos`;
  }

  private tarefasUrl(pesCod: number, prjCod: number, objCod: number, incCod?: number | null): string {
    return `${this.objetivosUrl(pesCod, prjCod, incCod)}/${objCod}/tarefas`;
  }

  private evidenciasUrl(pesCod: number, prjCod: number, objCod: number, trfCod: number, incCod?: number | null): string {
    return `${this.tarefasUrl(pesCod, prjCod, objCod, incCod)}/${trfCod}/evidencias`;
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
  findProjetos(pesCod: number, incCod?: number | null): Observable<ProjetoDTO[]> {
    return this.http.get<ProjetoDTO[]>(this.projetosUrl(pesCod, incCod));
  }
  findByProjetoId(pesCod: number, prjCod: number, incCod?: number | null): Observable<ProjetoDTO> {
    return this.http.get<ProjetoDTO>(`${this.projetosUrl(pesCod, incCod)}/${prjCod}`);
  }
  saveProjeto(pesCod: number, body: ProjetoDTO, incCod?: number | null): Observable<ProjetoDTO> {
    return this.http.post<ProjetoDTO>(this.projetosUrl(pesCod, incCod), body);
  }
  updateProjeto(pesCod: number, prjCod: number, body: ProjetoDTO, incCod?: number | null): Observable<ProjetoDTO> {
    return this.http.put<ProjetoDTO>(`${this.projetosUrl(pesCod, incCod)}/${prjCod}`, body);
  }
  deleteProjeto(pesCod: number, prjCod: number, incCod?: number | null): Observable<void> {
    return this.http.delete<void>(`${this.projetosUrl(pesCod, incCod)}/${prjCod}`);
  }

  // Objetivos
  findObjetivos(pesCod: number, prjCod: number, incCod?: number | null): Observable<ObjetivoDTO[]> {
    return this.http.get<ObjetivoDTO[]>(this.objetivosUrl(pesCod, prjCod, incCod));
  }
  findByObjetivoId(pesCod: number, prjCod: number, objCod: number, incCod?: number | null): Observable<ObjetivoDTO> {
    return this.http.get<ObjetivoDTO>(`${this.objetivosUrl(pesCod, prjCod, incCod)}/${objCod}`);
  }
  saveObjetivo(pesCod: number, prjCod: number, body: ObjetivoDTO, incCod?: number | null): Observable<ObjetivoDTO> {
    return this.http.post<ObjetivoDTO>(this.objetivosUrl(pesCod, prjCod, incCod), body);
  }
  updateObjetivo(pesCod: number, prjCod: number, objCod: number, body: ObjetivoDTO, incCod?: number | null): Observable<ObjetivoDTO> {
    return this.http.put<ObjetivoDTO>(`${this.objetivosUrl(pesCod, prjCod, incCod)}/${objCod}`, body);
  }
  deleteObjetivo(pesCod: number, prjCod: number, objCod: number, incCod?: number | null): Observable<void> {
    return this.http.delete<void>(`${this.objetivosUrl(pesCod, prjCod, incCod)}/${objCod}`);
  }

  // Tarefas
  findTarefas(pesCod: number, prjCod: number, objCod: number, incCod?: number | null): Observable<TarefaDTO[]> {
    return this.http.get<TarefaDTO[]>(this.tarefasUrl(pesCod, prjCod, objCod, incCod));
  }
  findByTarefaId(pesCod: number, prjCod: number, objCod: number, trfCod: number, incCod?: number | null): Observable<TarefaDTO> {
    return this.http.get<TarefaDTO>(`${this.tarefasUrl(pesCod, prjCod, objCod, incCod)}/${trfCod}`);
  }
  saveTarefa(pesCod: number, prjCod: number, objCod: number, body: TarefaDTO, incCod?: number | null): Observable<TarefaDTO> {
    return this.http.post<TarefaDTO>(this.tarefasUrl(pesCod, prjCod, objCod, incCod), body);
  }
  updateTarefa(pesCod: number, prjCod: number, objCod: number, trfCod: number, body: TarefaDTO, incCod?: number | null): Observable<TarefaDTO> {
    return this.http.put<TarefaDTO>(`${this.tarefasUrl(pesCod, prjCod, objCod, incCod)}/${trfCod}`, body);
  }
  deleteTarefa(pesCod: number, prjCod: number, objCod: number, trfCod: number, incCod?: number | null): Observable<void> {
    return this.http.delete<void>(`${this.tarefasUrl(pesCod, prjCod, objCod, incCod)}/${trfCod}`);
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
      `${this.tarefasUrl(pesCod, prjCod, objCod, incCod)}/${trfCod}/avaliacao`,
      body,
    );
  }

  // Evidências
  findEvidencias(pesCod: number, prjCod: number, objCod: number, trfCod: number, incCod?: number | null): Observable<EvidenciaDTO[]> {
    return this.http.get<EvidenciaDTO[]>(this.evidenciasUrl(pesCod, prjCod, objCod, trfCod, incCod));
  }
  saveEvidencia(pesCod: number, prjCod: number, objCod: number, trfCod: number, body: EvidenciaDTO, incCod?: number | null): Observable<EvidenciaDTO> {
    return this.http.post<EvidenciaDTO>(this.evidenciasUrl(pesCod, prjCod, objCod, trfCod, incCod), body);
  }
  updateEvidencia(pesCod: number, prjCod: number, objCod: number, trfCod: number, evdCod: number, body: EvidenciaDTO, incCod?: number | null): Observable<EvidenciaDTO> {
    return this.http.post<EvidenciaDTO>(`${this.evidenciasUrl(pesCod, prjCod, objCod, trfCod, incCod)}/${evdCod}`, body);
  }
  deleteEvidencia(pesCod: number, prjCod: number, objCod: number, trfCod: number, evdCod: number, incCod?: number | null): Observable<void> {
    return this.http.delete<void>(`${this.evidenciasUrl(pesCod, prjCod, objCod, trfCod, incCod)}/${evdCod}`);
  }
}
