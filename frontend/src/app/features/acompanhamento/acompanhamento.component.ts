import { DatePipe } from '@angular/common';
import { Component, computed, effect, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { PlanejamentoService } from '../../core/services/planejamento/planejamento.service';
import { CicloReadonlyBannerComponent } from '../ciclos/ciclo-readonly-banner.component';
import {
  AtividadePlanejada,
  EStatusAtividade,
  PlanPratica,
  PlanProcesso,
  STATUS_ATIVIDADE_LABEL,
  STATUS_PLANEJAMENTO_LABEL,
} from '../../models/planejamento/planejamento.model';
import {
  FILTROS_VAZIO,
  FiltrosState,
  FiltrosBarComponent,
  OpcaoPratica,
  OpcaoProcesso,
  OpcaoStatus,
} from '../../shared/ui/filtros-bar/filtros-bar.component';
import { AtividadeRegistroDialog } from './atividade-registro.dialog';

/** Estados que podem ser filtrados (ATRASADA é derivado do prazo). */
const STATUS_FILTRAVEIS: EStatusAtividade[] = ['PLANEJADA', 'EM_ANDAMENTO', 'CONCLUIDA', 'ATRASADA'];

/**
 * Tela "Acompanhamento de execução": mesma estrutura do planejamento (cabeçalho + accordion de
 * processos/práticas/atividades), porém só leitura da estrutura. Cada processo mostra o total de
 * atividades e quantas estão concluídas; a barra de filtros padrão (Processo/Prática/Responsável/
 * Status) recorta a árvore exibida; o botão "Registrar" de cada atividade abre o modal para mudar o
 * status e avaliar as evidências.
 */
@Component({
  selector: 'app-acompanhamento',
  imports: [
    DatePipe,
    MatCardModule,
    MatExpansionModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatDialogModule,
    FiltrosBarComponent,
    CicloReadonlyBannerComponent,
  ],
  templateUrl: './acompanhamento.component.html',
  styleUrls: ['./acompanhamento.component.css', '../shared/arvore-processos.css'],
})
export class AcompanhamentoComponent {
  private readonly service = inject(PlanejamentoService);
  private readonly dialog = inject(MatDialog);
  private readonly route = inject(ActivatedRoute);

  /** Deep-link do Painel Operacional já tratado? (evita reabrir quando a estrutura recarrega). */
  private registroAberto = false;

  readonly statusPlanoLabel = STATUS_PLANEJAMENTO_LABEL;
  readonly statusLabel = STATUS_ATIVIDADE_LABEL;

  readonly atualRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.atual(),
  });
  readonly estruturaRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.estrutura(),
  });

  readonly plano = computed(() => this.atualRes.value()?.planejamento ?? null);

  // ---- filtros padrão ----
  readonly filtros = signal<FiltrosState>({ ...FILTROS_VAZIO });

  readonly statusOpcoes: OpcaoStatus[] = STATUS_FILTRAVEIS.map(s => ({
    value: s,
    label: STATUS_ATIVIDADE_LABEL[s],
  }));

  readonly processoOpcoes = computed<OpcaoProcesso[]>(() =>
    (this.estruturaRes.value() ?? []).map(p => ({ nome: p.nome })),
  );

  readonly praticaOpcoes = computed<OpcaoPratica[]>(() => {
    const out: OpcaoPratica[] = [];
    for (const proc of this.estruturaRes.value() ?? []) {
      for (const pr of proc.praticas) {
        out.push({ nome: pr.nome, processoNome: proc.nome });
      }
    }
    return out;
  });

  /** Árvore com os filtros aplicados; sem filtro, devolve a estrutura original (mantém práticas vazias). */
  readonly estruturaFiltrada = computed<PlanProcesso[]>(() => {
    const f = this.filtros();
    const procs = this.estruturaRes.value() ?? [];
    const algum =
      f.processo != null || f.pratica != null || f.respPesCod != null || f.status != null;
    if (!algum) {
      return procs;
    }
    const out: PlanProcesso[] = [];
    for (const proc of procs) {
      if (f.processo != null && proc.nome !== f.processo) {
        continue;
      }
      const praticas: PlanPratica[] = [];
      for (const pr of proc.praticas) {
        if (f.pratica != null && pr.nome !== f.pratica) {
          continue;
        }
        const atividades = pr.atividades.filter(
          a =>
            (f.respPesCod == null || a.respPesCod === f.respPesCod) &&
            (f.status == null || a.status === f.status),
        );
        if (atividades.length > 0) {
          praticas.push({ ...pr, atividades });
        }
      }
      if (praticas.length > 0) {
        out.push({ ...proc, praticas });
      }
    }
    return out;
  });

  constructor() {
    // Vindo do Painel Operacional (?registrar=atpCod): abre o modal da atividade quando a estrutura
    // estiver carregada. Só uma vez — recarregar a estrutura não deve reabrir o modal.
    effect(() => {
      const processos = this.estruturaRes.value();
      if (!processos || this.registroAberto) {
        return;
      }
      const atpCod = Number(this.route.snapshot.queryParamMap.get('registrar'));
      if (!atpCod) {
        return;
      }
      const atividade = processos
        .flatMap(p => p.praticas)
        .flatMap(pr => pr.atividades)
        .find(a => a.atpCod === atpCod);
      if (atividade) {
        this.registroAberto = true;
        this.registrar(atividade);
      }
    });
  }

  private atividadesDe(proc: PlanProcesso): AtividadePlanejada[] {
    return proc.praticas.flatMap(pr => pr.atividades);
  }

  /** Total de atividades exibidas no processo (reflete os filtros ativos). */
  total(proc: PlanProcesso): number {
    return this.atividadesDe(proc).length;
  }

  /** Atividades concluídas exibidas no processo (numerador do cabeçalho). */
  concluidas(proc: PlanProcesso): number {
    return this.atividadesDe(proc).filter(a => a.status === 'CONCLUIDA').length;
  }

  registrar(atv: AtividadePlanejada): void {
    this.dialog.open(AtividadeRegistroDialog, {
      width: '640px',
      data: { atividade: atv },
    });
  }
}
