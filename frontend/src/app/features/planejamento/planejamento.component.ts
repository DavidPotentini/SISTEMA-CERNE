import { DatePipe } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
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
  STATUS_PLANEJAMENTO_LABEL,
} from '../../models/planejamento/planejamento.model';
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
