import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { of } from 'rxjs';
import { CicloService } from '../../core/services/ciclo/ciclo.service';
import { Ciclo, STATUS_CICLO_LABEL } from '../../models/ciclo/ciclo.model';
import { EmpreendimentoService } from '../../core/services/empreendimento/empreendimento.service';
import { PlanejamentoService } from '../../core/services/planejamento/planejamento.service';
import { dataParaIso, isoParaData } from '../../shared/util/data';
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
    MatDatepickerModule,
    MatTabsModule,
  ],
  templateUrl: './empreendimento-gerenciar.dialog.html',
  styleUrl: './empreendimento-gerenciar.dialog.css',
})
export class EmpreendimentoGerenciarDialog {
  private readonly service = inject(EmpreendimentoService);
  private readonly cicloService = inject(CicloService);
  private readonly planejamentoService = inject(PlanejamentoService);
  private readonly ref = inject(MatDialogRef<EmpreendimentoGerenciarDialog>);
  readonly empreendimento = inject<Empreendimento | null>(MAT_DIALOG_DATA);
  readonly novo = this.empreendimento === null;

  readonly gerandoAtividades = signal(false);
  readonly podePublicarAtividades = computed(() => !this.novo && this.empreendimento?.cicCod != null);

  private readonly ciclosRes = rxResource({
    params: () => ({}),
    stream: () => this.cicloService.listar(),
  });
  readonly cicloAtivo = computed<Ciclo | null>(() => {
    const ciclos = this.ciclosRes.value() ?? [];
    return ciclos.find(c => c.emFoco) ?? ciclos.find(c => c.status === 'ATIVO') ?? null;
  });
  readonly cicCod = signal<number | null>(this.empreendimento?.cicCod ?? null);

  readonly estagios = Object.keys(ESTAGIO_LABEL) as EEstagioIncubacao[];
  readonly statusOpcoes = Object.keys(STATUS_EMP_LABEL) as EStatusEmpreendimento[];
  readonly situacoesContrato = Object.keys(SITUACAO_CONTRATO_LABEL) as ESituacaoContrato[];
  readonly niveisMaturidade = Object.keys(NIVEL_MATURIDADE_LABEL) as ENivelMaturidade[];
  readonly estagioLabel = ESTAGIO_LABEL;
  readonly statusLabel = STATUS_EMP_LABEL;
  readonly situacaoContratoLabel = SITUACAO_CONTRATO_LABEL;
  readonly nivelMaturidadeLabel = NIVEL_MATURIDADE_LABEL;

  readonly colunas = computed(() =>
    this.novo ? ['nome', 'email', 'telefone', 'acoes'] : ['nome', 'email', 'telefone'],
  );

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  protected readonly isoParaData = isoParaData;
  protected readonly dataParaIso = dataParaIso;

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
  readonly inicioContrato = signal(this.empreendimento?.inicioContrato ?? '');
  readonly fimContrato = signal(this.empreendimento?.fimContrato ?? '');

  private readonly pessoasVersao = signal(0);
  readonly pessoas = rxResource({
    params: () => ({ empCod: this.empreendimento?.empCod, versao: this.pessoasVersao() }),
    stream: ({ params }) =>
      params.empCod == null ? of([]) : this.service.listarPessoas(params.empCod),
  });
  readonly pessoasLocais = signal<PessoaRascunho[]>([]);

  readonly pessoasExibidas = computed<PessoaRascunho[]>(() =>
    this.novo ? this.pessoasLocais() : (this.pessoas.value() ?? []),
  );

  readonly statusCicloLabel = STATUS_CICLO_LABEL;
  readonly ciclos = rxResource({
    params: () => ({ empCod: this.empreendimento?.empCod }),
    stream: ({ params }) =>
      params.empCod == null ? of<Ciclo[]>([]) : this.service.listarCiclos(params.empCod),
  });

  readonly colunasDoc = ['nome', 'acoes'];
  readonly enviandoDoc = signal(false);
  private readonly documentosVersao = signal(0);
  readonly documentos = rxResource({
    params: () => ({ empCod: this.empreendimento?.empCod, v: this.documentosVersao() }),
    stream: ({ params }) =>
      params.empCod == null ? of([]) : this.service.listarDocumentos(params.empCod),
  });

  fmtCiclo(d: string | null): string {
    return d ? d.split('-').reverse().join('/') : '—';
  }

  aoSelecionarArquivo(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';
    if (!file || this.empreendimento == null) return;
    this.enviandoDoc.set(true);
    this.service.anexarDocumento(this.empreendimento.empCod, file).subscribe({
      next: () => {
        this.enviandoDoc.set(false);
        this.documentosVersao.update(v => v + 1);
      },
      error: e => {
        this.enviandoDoc.set(false);
        window.alert(e?.error?.mensagem ?? 'Falha ao anexar o documento.');
      },
    });
  }

  removerDocumento(arqCod: number): void {
    if (this.empreendimento == null) return;
    if (!window.confirm('Remover este documento?')) return;
    this.service.removerDocumento(this.empreendimento.empCod, arqCod).subscribe({
      next: () => this.documentosVersao.update(v => v + 1),
      error: e => window.alert(e?.error?.mensagem ?? 'Falha ao remover o documento.'),
    });
  }

  publicarAtividades(): void {
    const empCod = this.empreendimento?.empCod;
    if (empCod == null) return;
    this.gerandoAtividades.set(true);
    this.planejamentoService.gerarAtividadesEmpreendimento(empCod).subscribe({
      next: () => {
        this.gerandoAtividades.set(false);
        this.planejamentoService.recarregar();
        window.alert('Atividades por empreendimento geradas no ciclo.');
      },
      error: e => {
        this.gerandoAtividades.set(false);
        window.alert(e?.error?.mensagem ?? 'Falha ao gerar as atividades.');
      },
    });
  }

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
      inicioContrato: this.inicioContrato() || null,
      fimContrato: this.fimContrato() || null,
      cicCod: this.cicCod(),
    };
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

  removerLocal(indice: number): void {
    this.pessoasLocais.update(lista => lista.filter((_, i) => i !== indice));
  }
}
