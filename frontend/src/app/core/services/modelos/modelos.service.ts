import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { EAtivoInativo } from '../../../models/metodologia/metodologia.model';
import {
  AtividadeModelo,
  Modelo,
  ModeloProcesso,
} from '../../../models/modelos/modelo.model';

/**
 * Modelos de planejamento da própria incubadora (tenant vem do JWT). A estrutura de processos/práticas
 * é herdada da metodologia; aqui só as atividades são editáveis, e apenas em modelos RASCUNHO.
 */
@Injectable({ providedIn: 'root' })
export class ModelosService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/modelos`;

  /** Incrementa a cada mutação; listagens e estrutura observam para recarregar. */
  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  listarModelos() {
    return this.http.get<Modelo[]>(this.base);
  }

  obterModelo(modCod: number) {
    return this.http.get<Modelo>(`${this.base}/${modCod}`);
  }

  /** Cria um modelo com base na última metodologia VIGENTE (resolvida no backend). */
  criarModelo(dto: Partial<Modelo>) {
    return this.http.post<Modelo>(this.base, dto);
  }

  editarModelo(modCod: number, dto: Partial<Modelo>) {
    return this.http.put<Modelo>(`${this.base}/${modCod}`, dto);
  }

  /** Publica o modelo (imutável a partir daqui). */
  publicarModelo(modCod: number) {
    return this.http.post<Modelo>(`${this.base}/${modCod}/publicar`, null);
  }

  /** Estrutura (processos/práticas da versão base) com as atividades do modelo. */
  estrutura(modCod: number) {
    return this.http.get<ModeloProcesso[]>(`${this.base}/${modCod}/estrutura`);
  }

  adicionarAtividade(modCod: number, prtCod: number, dto: Partial<AtividadeModelo>) {
    return this.http.post<AtividadeModelo>(
      `${this.base}/${modCod}/praticas/${prtCod}/atividades`,
      dto,
    );
  }

  editarAtividade(modCod: number, atmCod: number, dto: Partial<AtividadeModelo>) {
    return this.http.put<AtividadeModelo>(`${this.base}/${modCod}/atividades/${atmCod}`, dto);
  }

  alterarSituacaoAtividade(modCod: number, atmCod: number, situacao: EAtivoInativo) {
    return this.http.patch<AtividadeModelo>(
      `${this.base}/${modCod}/atividades/${atmCod}/situacao`,
      null,
      { params: { situacao } },
    );
  }
}
