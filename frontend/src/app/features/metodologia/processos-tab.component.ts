import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import { Pratica, Processo } from '../../models/metodologia/metodologia.model';
import {
  FILTROS_VAZIO,
  FiltrosState,
  FiltrosBarComponent,
  OpcaoPratica,
  OpcaoProcesso,
} from '../../shared/ui/filtros-bar/filtros-bar.component';
import { ProcessoFormDialog } from './processo-form.dialog';
import { PraticaFormDialog } from './pratica-form.dialog';

/**
 * Aba "Processos e Práticas": accordions cuja ordem é definida arrastando (a ordem não aparece). Ao
 * expandir, veem-se as práticas e o botão de adicionar prática àquele processo. No topo, adicionar
 * novo processo. Edita a metodologia direto (sem versionamento).
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
  styleUrl: './processos-tab.component.css',
})
export class ProcessosTabComponent {
  private readonly service = inject(MetodologiaService);
  private readonly dialog = inject(MatDialog);

  /** Processos da metodologia; refaz a busca a cada mutação. */
  readonly processos = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listarProcessos(),
  });

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

  adicionarProcesso(): void {
    this.dialog.open(ProcessoFormDialog, { width: '520px' });
  }

  editarProcesso(p: Processo): void {
    this.dialog.open(ProcessoFormDialog, { width: '520px', data: { processo: p } });
  }

  alternarProcesso(p: Processo): void {
    const situacao = p.situacao === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    this.service.alterarSituacaoProcesso(p.prcCod, situacao).subscribe(() => this.service.recarregar());
  }

  adicionarPratica(p: Processo): void {
    this.dialog.open(PraticaFormDialog, { width: '520px', data: { prcCod: p.prcCod, processo: p.nome } });
  }

  editarPratica(p: Processo, pr: Pratica): void {
    this.dialog.open(PraticaFormDialog, {
      width: '520px',
      data: { prcCod: p.prcCod, processo: p.nome, pratica: pr },
    });
  }

  alternarPratica(p: Processo, pr: Pratica): void {
    const situacao = pr.situacao === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    this.service
      .alterarSituacaoPratica(p.prcCod, pr.prtCod, situacao)
      .subscribe(() => this.service.recarregar());
  }
}
