import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import {
  Empreendimento,
  NovoEmpreendimento,
  PessoaEmpreendimento,
} from '../../../models/empreendimento/empreendimento.model';

/**
 * Empreendimentos da própria incubadora (o schema do tenant vem do JWT). As pessoas do
 * empreendimento (membros da startup) são geridas no modal.
 */
@Injectable({ providedIn: 'root' })
export class EmpreendimentoService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/empreendimentos`;

  /** Incrementa a cada mutação; a lista observa para recarregar. */
  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  listar() {
    return this.http.get<Empreendimento[]>(this.base);
  }

  /** Cria o empreendimento; {@code dto.pessoas} (opcional) grava as pessoas iniciais no mesmo POST. */
  criar(dto: NovoEmpreendimento) {
    return this.http.post<Empreendimento>(this.base, dto);
  }

  editar(empCod: number, dto: Partial<Empreendimento>) {
    return this.http.put<Empreendimento>(`${this.base}/${empCod}`, dto);
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
