import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { PlanejamentoService } from '../../core/services/planejamento/planejamento.service';
import {
  AtividadePlanejada,
  Planejamento,
  PlanPratica,
  PlanProcesso,
  STATUS_PLANEJAMENTO_LABEL,
} from '../../models/planejamento/planejamento.model';
import {
  FILTROS_VAZIO,
  FiltrosState,
  FiltrosBarComponent,
  OpcaoPratica,
  OpcaoProcesso,
} from '../../shared/ui/filtros-bar/filtros-bar.component';
import { AtividadePlanejadaFormDialog } from './atividade-planejada-form.dialog';
import { ConsultarPublicacaoDialog } from './consultar-publicacao.dialog';
import { GerarModeloDialog } from './gerar-modelo.dialog';

/**
 * Tela "Planejamento institucional" do ciclo ativo. No máx. um planejamento vigente por ciclo:
 * "Gerar de modelo" cria (ou substitui) o plano a partir de um modelo publicado; "Consultar
 * publicação" mostra status/período/responsável/progresso. A estrutura de processos/práticas é
 * herdada da metodologia base (só leitura); as atividades podem ser ajustadas e complementares
 * incluídas/removidas enquanto o plano está publicado.
 */
@Component({
  selector: 'app-planejamento',
  imports: [
    DatePipe,
    MatCardModule,
    MatExpansionModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressBarModule,
    MatDialogModule,
    FiltrosBarComponent,
  ],
  templateUrl: './planejamento.component.html',
  styleUrl: './planejamento.component.css',
})
export class PlanejamentoComponent {
  private readonly service = inject(PlanejamentoService);
  private readonly dialog = inject(MatDialog);

  readonly statusLabel = STATUS_PLANEJAMENTO_LABEL;

  readonly atualRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.atual(),
  });
  readonly estruturaRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.estrutura(),
  });

  readonly plano = computed(() => this.atualRes.value()?.planejamento ?? null);
  /** Publicado = ciclo vigente, atividades editáveis. */
  readonly editavel = computed(() => this.plano()?.status === 'PUBLICADO');

  // ---- filtros padrão (sem status) ----
  readonly filtros = signal<FiltrosState>({ ...FILTROS_VAZIO });

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

  /**
   * Árvore com os filtros aplicados. Processo/prática recortam a estrutura (mantendo práticas vazias,
   * para ainda permitir incluir atividade); o filtro de responsável recorta atividades e esconde
   * práticas/processos que ficam sem atividade. Sem filtro, devolve a estrutura original.
   */
  readonly estruturaFiltrada = computed<PlanProcesso[]>(() => {
    const f = this.filtros();
    const procs = this.estruturaRes.value() ?? [];
    const algum = f.processo != null || f.pratica != null || f.respPesCod != null;
    if (!algum) {
      return procs;
    }
    const filtrarAtividade = f.respPesCod != null;
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
        let atividades = pr.atividades;
        if (filtrarAtividade) {
          atividades = atividades.filter(a => a.respPesCod === f.respPesCod);
          if (atividades.length === 0) {
            continue;
          }
        }
        praticas.push({ ...pr, atividades });
      }
      if (praticas.length === 0) {
        continue;
      }
      out.push({ ...proc, praticas });
    }
    return out;
  });

  gerar(): void {
    const atual = this.atualRes.value();
    this.dialog.open(GerarModeloDialog, {
      width: '520px',
      data: { cicloNome: atual?.cicloNome ?? null, substitui: this.plano() != null },
    });
  }

  consultar(): void {
    const plano = this.plano();
    if (!plano) return;
    this.dialog.open(ConsultarPublicacaoDialog, { width: '460px', data: plano });
  }

  adicionarAtividade(pr: PlanPratica): void {
    this.dialog.open(AtividadePlanejadaFormDialog, {
      width: '560px',
      data: { prtCod: pr.prtCod, pratica: pr.nome },
    });
  }

  ajustarAtividade(pr: PlanPratica, atv: AtividadePlanejada): void {
    this.dialog.open(AtividadePlanejadaFormDialog, {
      width: '560px',
      data: { prtCod: pr.prtCod, pratica: pr.nome, atividade: atv },
    });
  }

  removerAtividade(atv: AtividadePlanejada): void {
    this.service.removerComplementar(atv.atpCod).subscribe(() => this.service.recarregar());
  }

  progresso(p: Planejamento): number {
    return p.progresso;
  }
}
