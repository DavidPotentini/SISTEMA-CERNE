import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Arquivo } from '../../../models/evidencia/evidencia.model';
import { Ciclo } from '../../../models/ciclo/ciclo.model';
import {
  Empreendimento,
  NovoEmpreendimento,
  PessoaEmpreendimento,
} from '../../../models/empreendimento/empreendimento.model';

@Injectable({ providedIn: 'root' })
export class EmpreendimentoService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/empreendimentos`;

  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  listar() {
    return this.http.get<Empreendimento[]>(this.base);
  }

  listarDoCiclo() {
    return this.http.get<Empreendimento[]>(`${this.base}/ciclo`);
  }

  criar(dto: NovoEmpreendimento) {
    return this.http.post<Empreendimento>(this.base, dto);
  }

  editar(empCod: number, dto: Partial<Empreendimento>) {
    return this.http.put<Empreendimento>(`${this.base}/${empCod}`, dto);
  }

  listarCiclos(empCod: number) {
    return this.http.get<Ciclo[]>(`${this.base}/${empCod}/ciclos`);
  }

  listarDocumentos(empCod: number) {
    return this.http.get<Arquivo[]>(`${this.base}/${empCod}/documentos`);
  }

  anexarDocumento(empCod: number, arquivo: File) {
    const form = new FormData();
    form.append('arquivo', arquivo);
    return this.http.post<Arquivo>(`${this.base}/${empCod}/documentos`, form);
  }

  removerDocumento(empCod: number, arqCod: number) {
    return this.http.delete<void>(`${this.base}/${empCod}/documentos/${arqCod}`);
  }

  listarPessoas(empCod: number) {
    return this.http.get<PessoaEmpreendimento[]>(`${this.base}/${empCod}/pessoas`);
  }

  adicionarPessoa(empCod: number, dto: Partial<PessoaEmpreendimento>) {
    return this.http.post<PessoaEmpreendimento>(`${this.base}/${empCod}/pessoas`, dto);
  }

  definirRepresentanteLegal(empCod: number, pseCod: number) {
    return this.http.patch<PessoaEmpreendimento>(
      `${this.base}/${empCod}/pessoas/${pseCod}/representante-legal`,
      {},
    );
  }
}
