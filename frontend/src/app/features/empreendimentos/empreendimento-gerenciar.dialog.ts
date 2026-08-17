import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { of } from 'rxjs';
import { EmpreendimentoService } from '../../core/services/empreendimento/empreendimento.service';
import {
  EEstagioEmpreendimento,
  EModalidadeFisica,
  ESituacaoEmpreendimento,
  Empreendimento,
  ESTAGIO_LABEL,
  MODALIDADE_LABEL,
  SITUACAO_EMP_LABEL,
} from '../../models/empreendimento/empreendimento.model';

/**
 * Modal de empreendimento. Sem {@code empreendimento} = "Adicionar" (só os dados). Com
 * {@code empreendimento} = "Gerenciar": dados + responsável interno (equipe) e a listagem de
 * pessoas da startup, com cadastro ao topo e a opção de marcar o contato principal.
 */
@Component({
  selector: 'app-empreendimento-gerenciar',
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTableModule,
    MatIconModule,
  ],
  templateUrl: './empreendimento-gerenciar.dialog.html',
  styleUrl: './empreendimento-gerenciar.dialog.css',
})
export class EmpreendimentoGerenciarDialog {
  private readonly service = inject(EmpreendimentoService);
  private readonly ref = inject(MatDialogRef<EmpreendimentoGerenciarDialog>);
  /** Empreendimento a gerenciar, ou {@code null} para um novo. */
  readonly empreendimento = inject<Empreendimento | null>(MAT_DIALOG_DATA);
  readonly novo = this.empreendimento === null;

  readonly modalidades = Object.keys(MODALIDADE_LABEL) as EModalidadeFisica[];
  readonly estagios = Object.keys(ESTAGIO_LABEL) as EEstagioEmpreendimento[];
  readonly situacoes = Object.keys(SITUACAO_EMP_LABEL) as ESituacaoEmpreendimento[];
  readonly modalidadeLabel = MODALIDADE_LABEL;
  readonly estagioLabel = ESTAGIO_LABEL;
  readonly situacaoLabel = SITUACAO_EMP_LABEL;

  readonly colunas = ['nome', 'papel', 'contato'];

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly nome = signal(this.empreendimento?.nome ?? '');
  readonly setor = signal(this.empreendimento?.setor ?? '');
  readonly modalidadeFisica = signal<EModalidadeFisica | null>(
    this.empreendimento?.modalidadeFisica ?? null,
  );
  readonly estagio = signal<EEstagioEmpreendimento | null>(
    this.empreendimento?.estagio ?? 'IDEACAO',
  );
  readonly situacao = signal<ESituacaoEmpreendimento>(this.empreendimento?.situacao ?? 'EM_ANALISE');
  readonly entrada = signal(this.empreendimento?.entrada ?? '');
  readonly respPesCod = signal<number | null>(this.empreendimento?.respPesCod ?? null);

  /** Equipe da incubadora para o seletor de responsável interno. */
  readonly responsaveis = rxResource({ stream: () => this.service.listarResponsaveis() });

  // ---- pessoas do empreendimento (só no modo gerenciar) ----
  private readonly pessoasVersao = signal(0);
  readonly pessoas = rxResource({
    params: () => ({ empCod: this.empreendimento?.empCod, versao: this.pessoasVersao() }),
    stream: ({ params }) =>
      params.empCod == null ? of([]) : this.service.listarPessoas(params.empCod),
  });

  readonly cadastrando = signal(false);
  readonly pessoaNome = signal('');
  readonly pessoaPapel = signal('');
  readonly pessoaContato = signal('');

  salvar(): void {
    if (!this.nome().trim()) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<Empreendimento> = {
      nome: this.nome().trim(),
      setor: this.setor().trim() || null,
      modalidadeFisica: this.modalidadeFisica(),
      estagio: this.estagio(),
      situacao: this.situacao(),
      entrada: this.entrada() || null,
      respPesCod: this.respPesCod(),
    };
    const requisicao = this.novo
      ? this.service.criar(dto)
      : this.service.editar(this.empreendimento!.empCod, dto);
    requisicao.subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao salvar o empreendimento.');
      },
    });
  }

  abrirCadastro(): void {
    this.pessoaNome.set('');
    this.pessoaPapel.set('');
    this.pessoaContato.set('');
    this.cadastrando.set(true);
  }

  cadastrarPessoa(): void {
    const empCod = this.empreendimento?.empCod;
    if (empCod == null || !this.pessoaNome().trim()) return;
    this.service
      .adicionarPessoa(empCod, {
        nome: this.pessoaNome().trim(),
        papel: this.pessoaPapel().trim() || null,
        contato: this.pessoaContato().trim() || null,
        principal: false
      })
      .subscribe(() => {
        this.cadastrando.set(false);
        this.pessoasVersao.update(v => v + 1);
      });
  }

  tornarPrincipal(pseCod: number): void {
    const empCod = this.empreendimento?.empCod;
    if (empCod == null) return;
    this.service
      .definirPrincipal(empCod, pseCod)
      .subscribe(() => this.pessoasVersao.update(v => v + 1));
  }
}
