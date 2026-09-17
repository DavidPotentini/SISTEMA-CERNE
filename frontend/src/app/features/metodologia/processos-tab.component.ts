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

export interface GrupoView {
  agrCod: number | null;
  nome: string;
  fonte: Agrupamento | null;
  atividades: AtividadeMetodologia[];
}

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

  readonly nivelLabel = NIVEL_CERNE_LABEL;

  readonly processos = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listarProcessos(),
  }));

  readonly atividades = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listarAtividades(),
  }));

  readonly agrupamentos = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listarAgrupamentos(),
  }));

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

  agrupamentosDa(prtCod: number): Agrupamento[] {
    return this.agrupamentosPorPratica().get(prtCod) ?? [];
  }

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

  gruposDaPraticaFiltrados(prtCod: number): GrupoView[] {
    const f = this.filtros();
    const grupos = this.gruposDaPratica(prtCod);
    if (f.agrupamento == null && f.atividade == null) {
      return grupos;
    }
    const out: GrupoView[] = [];
    for (const g of grupos) {
      if (f.agrupamento != null && !g.nome.toLowerCase().includes(f.agrupamento.toLowerCase())) {
        continue;
      }
      let atividades = g.atividades;
      if (f.atividade != null) {
        const busca = f.atividade.toLowerCase();
        atividades = atividades.filter(a => a.nome.toLowerCase().includes(busca));
        if (atividades.length === 0) {
          continue;
        }
      }
      out.push({ ...g, atividades });
    }
    return out;
  }

  readonly tudoExpandido = signal(true);

  alternarTudo(): void {
    this.tudoExpandido.update(v => !v);
  }

  private readonly gruposRecolhidos = signal<Set<string>>(new Set());

  private chaveGrupo(prtCod: number, g: GrupoView): string {
    return `${prtCod}:${g.agrCod ?? 'sem'}`;
  }

  grupoRecolhido(prtCod: number, g: GrupoView): boolean {
    if (this.filtroAtivoExpandeGp()) {
      return false;
    }
    return !this.gruposRecolhidos().has(this.chaveGrupo(prtCod, g));
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

  readonly filtros = signal<FiltrosState>({ ...FILTROS_VAZIO });

  readonly algumFiltroAtivo = computed<boolean>(() => {
    const f = this.filtros();
    return f.processo != null || f.pratica != null || f.agrupamento != null || f.atividade != null;
  });

  readonly filtroAtivoExpandeGp = computed<boolean>(() => {
    const f = this.filtros();
    return f.atividade != null;
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

  readonly processosFiltrados = computed<Processo[]>(() => {
    const f = this.filtros();
    const lista = this.processos.value() ?? [];
    if (!this.algumFiltroAtivo()) {
      return lista;
    }
    const textoAtivo = f.agrupamento != null || f.atividade != null;
    const out: Processo[] = [];
    for (const p of lista) {
      if (f.processo != null && p.nome !== f.processo) {
        continue;
      }
      let praticas = p.praticas;
      if (f.pratica != null) {
        praticas = praticas.filter(pr => pr.nome === f.pratica);
      }
      if (textoAtivo) {
        praticas = praticas.filter(pr => this.gruposDaPraticaFiltrados(pr.prtCod).length > 0);
      }
      if (praticas.length === 0) {
        continue;
      }
      out.push({ ...p, praticas });
    }
    return out;
  });

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

  reordenarPratica(p: Processo, event: CdkDragDrop<Pratica[]>): void {
    if (event.previousIndex === event.currentIndex) {
      return;
    }
    moveItemInArray(p.praticas, event.previousIndex, event.currentIndex);
    const prtCods = p.praticas.map(pr => pr.prtCod);
    this.service.reordenarPraticas(p.prcCod, prtCods).subscribe(() => this.service.recarregar());
  }

  // Persiste a sequência de `ameCod` da prática inteira (grupos na ordem + "sem agrupamento" no fim),
  // com o grupo movido reordenado internamente.
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

  // O grupo sintético "Sem agrupamento" (sempre o último e não arrastável) fica fora: o índice de
  // destino é limitado aos grupos reais.
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
