import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import { reterRecurso } from '../../shared/util/reter-recurso';
import {
  Agrupamento,
  AtividadeMetodologia,
  NIVEL_CERNE_LABEL,
  Pratica,
  Processo,
} from '../../models/metodologia/metodologia.model';
import {
  FILTROS_VAZIO,
  FiltrosState,
  FiltrosBarComponent,
  OpcaoPratica,
  OpcaoProcesso,
} from '../../shared/ui/filtros-bar/filtros-bar.component';
import { ProcessoFormDialog } from './processo-form.dialog';
import { PraticaFormDialog } from './pratica-form.dialog';
import { AgrupamentoFormDialog } from './agrupamento-form.dialog';
import { AtividadeFormDialog } from './atividade-form.dialog';

/** Grupo (sub-plano) montado para exibição: um agrupamento real (`fonte`), ou o sintético "Sem agrupamento". */
export interface GrupoView {
  agrCod: number | null;
  nome: string;
  /** Agrupamento de origem; `null` no grupo sintético "Sem agrupamento". */
  fonte: Agrupamento | null;
  atividades: AtividadeMetodologia[];
}

/**
 * Aba "Processos e Práticas": accordions cuja ordem é definida arrastando (a ordem não aparece). Ao
 * expandir, veem-se as práticas — cada uma com suas atividades-padrão agrupadas embaixo — e os botões
 * de adicionar prática e atividade. No topo, adicionar novo processo. Edita a metodologia direto (sem
 * versionamento).
 */
@Component({
  selector: 'app-processos-tab',
  imports: [
    MatExpansionModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatTooltipModule,
    DragDropModule,
    FiltrosBarComponent,
  ],
  templateUrl: './processos-tab.component.html',
  styleUrls: ['./processos-tab.component.css', '../shared/arvore-processos.css'],
})
export class ProcessosTabComponent {
  private readonly service = inject(MetodologiaService);
  private readonly dialog = inject(MatDialog);

  /** Rótulo do nível CERNE do processo (por ora só CERNE 1). */
  readonly nivelLabel = NIVEL_CERNE_LABEL;

  /** Processos da metodologia; refaz a busca a cada mutação. */
  readonly processos = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listarProcessos(),
  }));

  /** Atividades-padrão da metodologia; agrupadas por prática no template. */
  readonly atividades = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listarAtividades(),
  }));

  /** Agrupamentos (sub-planos) da metodologia; ordenados por `ordem` dentro da prática. */
  readonly agrupamentos = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listarAgrupamentos(),
  }));

  /** Atividades indexadas por `prtCod`, para renderizar sob cada prática. */
  readonly atividadesPorPratica = computed<Map<number, AtividadeMetodologia[]>>(() => {
    const mapa = new Map<number, AtividadeMetodologia[]>();
    for (const a of this.atividades.value() ?? []) {
      const lista = mapa.get(a.prtCod);
      if (lista != null) {
        lista.push(a);
      } else {
        mapa.set(a.prtCod, [a]);
      }
    }
    return mapa;
  });

  /** Agrupamentos indexados por `prtCod` (mantêm a ordem vinda do backend). */
  readonly agrupamentosPorPratica = computed<Map<number, Agrupamento[]>>(() => {
    const mapa = new Map<number, Agrupamento[]>();
    for (const g of this.agrupamentos.value() ?? []) {
      const lista = mapa.get(g.prtCod);
      if (lista != null) {
        lista.push(g);
      } else {
        mapa.set(g.prtCod, [g]);
      }
    }
    return mapa;
  });

  atividadesDa(prtCod: number): AtividadeMetodologia[] {
    return this.atividadesPorPratica().get(prtCod) ?? [];
  }

  /** Agrupamentos reais (ATIVO/INATIVO) de uma prática, na ordem. */
  agrupamentosDa(prtCod: number): Agrupamento[] {
    return this.agrupamentosPorPratica().get(prtCod) ?? [];
  }

  /**
   * Grupos de uma prática para exibição: cada agrupamento real com suas atividades, seguido do grupo
   * sintético "Sem agrupamento" (atividades com `agrCod` nulo) quando houver.
   */
  gruposDaPratica(prtCod: number): GrupoView[] {
    const atividades = this.atividadesDa(prtCod);
    const grupos: GrupoView[] = this.agrupamentosDa(prtCod).map(g => ({
      agrCod: g.agrCod,
      nome: g.nome,
      fonte: g,
      atividades: atividades.filter(a => a.agrCod === g.agrCod),
    }));
    const semGrupo = atividades.filter(a => a.agrCod == null);
    if (semGrupo.length > 0) {
      grupos.push({ agrCod: null, nome: 'Sem agrupamento', fonte: null, atividades: semGrupo });
    }
    return grupos;
  }

  /** Painéis abertos por padrão; o botão recolhe/expande a árvore toda de uma vez. */
  readonly tudoExpandido = signal(true);

  alternarTudo(): void {
    this.tudoExpandido.update(v => !v);
  }

  /** Subgrupos recolhidos (esconde as atividades); chave = `prtCod:agrCod` (ou `:sem` no sintético). */
  private readonly gruposRecolhidos = signal<Set<string>>(new Set());

  private chaveGrupo(prtCod: number, g: GrupoView): string {
    return `${prtCod}:${g.agrCod ?? 'sem'}`;
  }

  grupoRecolhido(prtCod: number, g: GrupoView): boolean {
    return this.gruposRecolhidos().has(this.chaveGrupo(prtCod, g));
  }

  alternarRecolherGrupo(prtCod: number, g: GrupoView): void {
    const chave = this.chaveGrupo(prtCod, g);
    const set = new Set(this.gruposRecolhidos());
    if (set.has(chave)) {
      set.delete(chave);
    } else {
      set.add(chave);
    }
    this.gruposRecolhidos.set(set);
  }

  // ---- filtros padrão (só processo/prática) ----
  readonly filtros = signal<FiltrosState>({ ...FILTROS_VAZIO });

  readonly algumFiltro = computed<boolean>(() => {
    const f = this.filtros();
    return f.processo != null || f.pratica != null;
  });

  readonly processoOpcoes = computed<OpcaoProcesso[]>(() =>
    (this.processos.value() ?? []).map(p => ({ nome: p.nome })),
  );

  readonly praticaOpcoes = computed<OpcaoPratica[]>(() => {
    const out: OpcaoPratica[] = [];
    for (const p of this.processos.value() ?? []) {
      for (const pr of p.praticas) {
        out.push({ nome: pr.nome, processoNome: p.nome });
      }
    }
    return out;
  });

  /** Processos com os filtros aplicados; sem filtro, devolve a lista original (drag habilitado). */
  readonly processosFiltrados = computed<Processo[]>(() => {
    const f = this.filtros();
    const lista = this.processos.value() ?? [];
    if (!this.algumFiltro()) {
      return lista;
    }
    const out: Processo[] = [];
    for (const p of lista) {
      if (f.processo != null && p.nome !== f.processo) {
        continue;
      }
      let praticas = p.praticas;
      if (f.pratica != null) {
        praticas = praticas.filter(pr => pr.nome === f.pratica);
        if (praticas.length === 0) {
          continue;
        }
      }
      out.push({ ...p, praticas });
    }
    return out;
  });

  /** Arrastar-e-soltar: reordena localmente e persiste a nova sequência de `prcCod`. */
  reordenar(event: CdkDragDrop<Processo[]>): void {
    const lista = this.processos.value();
    if (!lista || event.previousIndex === event.currentIndex) {
      return;
    }
    moveItemInArray(lista, event.previousIndex, event.currentIndex);
    const prcCods: number[] = [];
    for (const p of lista) {
      prcCods.push(p.prcCod);
    }
    this.service.reordenarProcessos(prcCods).subscribe(() => this.service.recarregar());
  }

  /** Arrastar-e-soltar de práticas dentro de um processo; persiste a nova sequência de `prtCod`. */
  reordenarPratica(p: Processo, event: CdkDragDrop<Pratica[]>): void {
    if (event.previousIndex === event.currentIndex) {
      return;
    }
    moveItemInArray(p.praticas, event.previousIndex, event.currentIndex);
    const prtCods = p.praticas.map(pr => pr.prtCod);
    this.service.reordenarPraticas(p.prcCod, prtCods).subscribe(() => this.service.recarregar());
  }

  /**
   * Arrastar-e-soltar de atividades DENTRO de um grupo; persiste a nova sequência de `ameCod` da prática
   * inteira (grupos na ordem + "sem agrupamento" no fim), com o grupo movido reordenado internamente.
   */
  reordenarAtividade(pr: Pratica, agrCod: number | null, event: CdkDragDrop<AtividadeMetodologia[]>): void {
    if (event.previousIndex === event.currentIndex) {
      return;
    }
    const ameCods: number[] = [];
    for (const g of this.gruposDaPratica(pr.prtCod)) {
      if (g.agrCod === agrCod) {
        const lista = [...g.atividades];
        moveItemInArray(lista, event.previousIndex, event.currentIndex);
        ameCods.push(...lista.map(a => a.ameCod));
      } else {
        ameCods.push(...g.atividades.map(a => a.ameCod));
      }
    }
    this.service.reordenarAtividades(pr.prtCod, ameCods).subscribe(() => this.service.recarregar());
  }

  /**
   * Arrastar-e-soltar de agrupamentos dentro de uma prática; persiste a nova sequência de `agrCod`. O
   * grupo sintético "Sem agrupamento" (sempre o último e não arrastável) fica fora: o índice de destino
   * é limitado aos grupos reais.
   */
  reordenarAgrupamento(pr: Pratica, event: CdkDragDrop<GrupoView[]>): void {
    const grupos = [...this.agrupamentosDa(pr.prtCod)];
    const to = Math.min(event.currentIndex, grupos.length - 1);
    if (event.previousIndex === to) {
      return;
    }
    moveItemInArray(grupos, event.previousIndex, to);
    this.service
      .reordenarAgrupamentos(pr.prtCod, grupos.map(g => g.agrCod))
      .subscribe(() => this.service.recarregar());
  }

  adicionarProcesso(): void {
    const ok = window.confirm(
      'Todos os processos do CERNE 1 já foram inseridos na metodologia. '
        + 'Deseja mesmo adicionar um processo novo?',
    );
    if (!ok) return;
    this.dialog.open(ProcessoFormDialog, { width: '90vw', maxWidth: '1200px' });
  }

  editarProcesso(p: Processo): void {
    this.dialog.open(ProcessoFormDialog, { width: '90vw', maxWidth: '1200px', data: { processo: p } });
  }

  alternarProcesso(p: Processo): void {
    const situacao = p.situacao === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    this.service.alterarSituacaoProcesso(p.prcCod, situacao).subscribe(() => this.service.recarregar());
  }

  adicionarPratica(p: Processo): void {
    const ok = window.confirm(
      'Todas as práticas do CERNE 1 já foram inseridas na metodologia. '
        + 'Deseja mesmo adicionar uma prática nova?',
    );
    if (!ok) return;
    this.dialog.open(PraticaFormDialog, { width: '90vw', maxWidth: '1200px', data: { prcCod: p.prcCod, processo: p.nome } });
  }

  editarPratica(p: Processo, pr: Pratica): void {
    this.dialog.open(PraticaFormDialog, {
      width: '90vw', maxWidth: '1200px',
      data: { prcCod: p.prcCod, processo: p.nome, pratica: pr },
    });
  }

  alternarPratica(p: Processo, pr: Pratica): void {
    const situacao = pr.situacao === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    this.service
      .alterarSituacaoPratica(p.prcCod, pr.prtCod, situacao)
      .subscribe(() => this.service.recarregar());
  }

  adicionarAgrupamento(pr: Pratica): void {
    this.dialog.open(AgrupamentoFormDialog, {
      width: '90vw', maxWidth: '1200px',
      data: { prtCod: pr.prtCod, pratica: pr.nome },
    });
  }

  editarAgrupamento(pr: Pratica, g: Agrupamento): void {
    this.dialog.open(AgrupamentoFormDialog, {
      width: '90vw', maxWidth: '1200px',
      data: { prtCod: pr.prtCod, pratica: pr.nome, agrupamento: g },
    });
  }

  alternarAgrupamento(g: Agrupamento): void {
    const situacao = g.situacao === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    this.service.alterarSituacaoAgrupamento(g.agrCod, situacao).subscribe(() => this.service.recarregar());
  }

  adicionarAtividade(pr: Pratica, agrCod: number | null = null): void {
    this.dialog.open(AtividadeFormDialog, {
      width: '90vw', maxWidth: '1200px',
      data: { prtCod: pr.prtCod, agrCod: agrCod ?? undefined },
    });
  }

  editarAtividade(a: AtividadeMetodologia): void {
    this.dialog.open(AtividadeFormDialog, { width: '90vw', maxWidth: '1200px', data: { atividade: a } });
  }

  alternarAtividade(a: AtividadeMetodologia): void {
    const situacao = a.situacao === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    this.service.alterarSituacaoAtividade(a.ameCod, situacao).subscribe(() => this.service.recarregar());
  }

  /** Liga/desliga "repetir por empreendimento" na atividade (uma cópia por incubada na geração). */
  alternarPorEmpreendimentoAtividade(a: AtividadeMetodologia): void {
    this.service
      .alterarPorEmpreendimentoAtividade(a.ameCod, !a.porEmpreendimento)
      .subscribe(() => this.service.recarregar());
  }

  excluirAtividade(a: AtividadeMetodologia): void {
    const ok = window.confirm(`Excluir a atividade "${a.nome}"? Esta ação não pode ser desfeita.`);
    if (!ok) return;
    this.service.excluirAtividade(a.ameCod).subscribe(() => this.service.recarregar());
  }

  excluirAgrupamento(g: Agrupamento): void {
    const ok = window.confirm(`Excluir o agrupamento "${g.nome}"? Esta ação não pode ser desfeita.`);
    if (!ok) return;
    this.service.excluirAgrupamento(g.agrCod).subscribe({
      next: () => this.service.recarregar(),
      error: e => window.alert(e?.error?.mensagem ?? 'Não foi possível excluir o agrupamento.'),
    });
  }
}
