import { Component, computed, inject, signal } from '@angular/core';
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
  EEstagioIncubacao,
  ENivelMaturidade,
  ESituacaoContrato,
  EStatusEmpreendimento,
  Empreendimento,
  NovoEmpreendimento,
  PessoaRascunho,
  ESTAGIO_LABEL,
  NIVEL_MATURIDADE_LABEL,
  SITUACAO_CONTRATO_LABEL,
  STATUS_EMP_LABEL,
} from '../../models/empreendimento/empreendimento.model';

/**
 * Modal de empreendimento. Sem {@code empreendimento} = "Adicionar", com = "Editar". Em ambos os modos
 * as pessoas da startup podem ser cadastradas no mesmo fluxo: na criação ficam num rascunho local e são
 * gravadas junto ao salvar (após o empreendimento existir); na edição são gravadas na hora.
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
  /** Empreendimento a editar, ou {@code null} para um novo. */
  readonly empreendimento = inject<Empreendimento | null>(MAT_DIALOG_DATA);
  readonly novo = this.empreendimento === null;

  readonly estagios = Object.keys(ESTAGIO_LABEL) as EEstagioIncubacao[];
  readonly statusOpcoes = Object.keys(STATUS_EMP_LABEL) as EStatusEmpreendimento[];
  readonly situacoesContrato = Object.keys(SITUACAO_CONTRATO_LABEL) as ESituacaoContrato[];
  readonly niveisMaturidade = Object.keys(NIVEL_MATURIDADE_LABEL) as ENivelMaturidade[];
  readonly estagioLabel = ESTAGIO_LABEL;
  readonly statusLabel = STATUS_EMP_LABEL;
  readonly situacaoContratoLabel = SITUACAO_CONTRATO_LABEL;
  readonly nivelMaturidadeLabel = NIVEL_MATURIDADE_LABEL;

  /** Coluna de remover só existe no rascunho (criação). */
  readonly colunas = computed(() =>
    this.novo ? ['nome', 'email', 'telefone', 'acoes'] : ['nome', 'email', 'telefone'],
  );

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly nome = signal(this.empreendimento?.nome ?? '');
  readonly cnpj = signal(this.empreendimento?.cnpj ?? '');
  readonly cnae = signal(this.empreendimento?.cnae ?? '');
  readonly atividadeEconomica = signal(this.empreendimento?.atividadeEconomica ?? '');
  readonly instagram = signal(this.empreendimento?.instagram ?? '');
  readonly site = signal(this.empreendimento?.site ?? '');
  readonly email = signal(this.empreendimento?.email ?? '');
  readonly situacaoContrato = signal<ESituacaoContrato | null>(
    this.empreendimento?.situacaoContrato ?? null,
  );
  readonly estagio = signal<EEstagioIncubacao | null>(this.empreendimento?.estagio ?? 'IDEACAO');
  readonly status = signal<EStatusEmpreendimento>(this.empreendimento?.status ?? 'ATIVO');
  readonly nivelMaturidade = signal<ENivelMaturidade | null>(
    this.empreendimento?.nivelMaturidade ?? null,
  );
  readonly entrada = signal(this.empreendimento?.entrada ?? '');
  readonly saida = signal(this.empreendimento?.saida ?? '');

  // ---- pessoas do empreendimento ----
  private readonly pessoasVersao = signal(0);
  /** Pessoas já gravadas (modo edição). */
  readonly pessoas = rxResource({
    params: () => ({ empCod: this.empreendimento?.empCod, versao: this.pessoasVersao() }),
    stream: ({ params }) =>
      params.empCod == null ? of([]) : this.service.listarPessoas(params.empCod),
  });
  /** Rascunho local das pessoas ao criar (gravadas no salvar). */
  readonly pessoasLocais = signal<PessoaRascunho[]>([]);

  /** O que a tabela mostra: rascunho na criação, servidor na edição. */
  readonly pessoasExibidas = computed<PessoaRascunho[]>(() =>
    this.novo ? this.pessoasLocais() : (this.pessoas.value() ?? []),
  );

  readonly cadastrando = signal(false);
  readonly pessoaNome = signal('');
  readonly pessoaEmail = signal('');
  readonly pessoaTelefone = signal('');

  salvar(): void {
    if (!this.nome().trim()) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: NovoEmpreendimento = {
      nome: this.nome().trim(),
      cnpj: this.cnpj().trim() || null,
      cnae: this.cnae().trim() || null,
      atividadeEconomica: this.atividadeEconomica().trim() || null,
      instagram: this.instagram().trim() || null,
      site: this.site().trim() || null,
      email: this.email().trim() || null,
      situacaoContrato: this.situacaoContrato(),
      estagio: this.estagio(),
      status: this.status(),
      nivelMaturidade: this.nivelMaturidade(),
      entrada: this.entrada() || null,
      saida: this.saida() || null,
    };
    // Na criação, as pessoas do rascunho vão no mesmo POST (gravadas junto no backend).
    const requisicao = this.novo
      ? this.service.criar({ ...dto, pessoas: this.pessoasLocais() })
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
    this.pessoaEmail.set('');
    this.pessoaTelefone.set('');
    this.cadastrando.set(true);
  }

  /** Na criação, empilha no rascunho local; na edição, grava direto no servidor. */
  cadastrarPessoa(): void {
    const nome = this.pessoaNome().trim();
    if (!nome) return;
    const pessoa: PessoaRascunho = {
      nome,
      email: this.pessoaEmail().trim() || null,
      telefone: this.pessoaTelefone().trim() || null,
    };
    if (this.novo) {
      this.pessoasLocais.update(lista => [...lista, pessoa]);
      this.cadastrando.set(false);
      return;
    }
    this.service.adicionarPessoa(this.empreendimento!.empCod, { ...pessoa, representanteLegal: false })
      .subscribe(() => {
        this.cadastrando.set(false);
        this.pessoasVersao.update(v => v + 1);
      });
  }

  /** Remove uma pessoa do rascunho (só na criação). */
  removerLocal(indice: number): void {
    this.pessoasLocais.update(lista => lista.filter((_, i) => i !== indice));
  }
}
