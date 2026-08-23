import { Component, inject, input, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MonitoramentoService } from '../../core/services/monitoramento/monitoramento.service';
import {
  Aplicacao,
  EEixoCerne,
  EIXO_LABEL,
  EIXOS,
  ERecomendacaoMonitor,
  EStatusMonitoramento,
  ESituacaoRodada,
  ETipoRodada,
  RECOMENDACAO_LABEL,
  Rodada,
  SITUACAO_RODADA_LABEL,
  STATUS_MONITORAMENTO_LABEL,
  TIPO_RODADA_LABEL,
} from '../../models/monitoramento/monitoramento.model';
import { RevisarAplicacaoDialog } from './revisar-aplicacao.dialog';

/**
 * Uma rodada na aba "Aplicações e pontuação": cabeçalho com tipo/situação e o botão "Concluir
 * rodada"; abaixo, um card por empreendimento com a nota de cada eixo CERNE, a recomendação do
 * monitor e o status do monitoramento. "Revisar" abre o modal de atribuição.
 */
@Component({
  selector: 'app-rodada-aplicacoes',
  imports: [
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatDialogModule,
  ],
  templateUrl: './rodada-aplicacoes.component.html',
  styleUrl: './rodada-aplicacoes.component.css',
})
export class RodadaAplicacoesComponent {
  private readonly service = inject(MonitoramentoService);
  private readonly dialog = inject(MatDialog);

  readonly rodada = input.required<Rodada>();

  readonly eixos = EIXOS;
  readonly concluindo = signal(false);

  readonly aplicacoesRes = rxResource({
    params: () => ({ v: this.service.versao(), rod: this.rodada().rodCod }),
    stream: () => this.service.aplicacoes(this.rodada().rodCod),
  });

  tipoLabel(t: ETipoRodada): string {
    return TIPO_RODADA_LABEL[t];
  }
  situacaoLabel(s: ESituacaoRodada): string {
    return SITUACAO_RODADA_LABEL[s];
  }
  statusLabel(s: EStatusMonitoramento): string {
    return STATUS_MONITORAMENTO_LABEL[s];
  }
  recomendacaoLabel(r: ERecomendacaoMonitor): string {
    return RECOMENDACAO_LABEL[r];
  }
  eixoLabel(e: EEixoCerne): string {
    return EIXO_LABEL[e];
  }

  /** Nota do eixo na aplicação, ou {@code null} se não pontuado. */
  notaDoEixo(aplicacao: Aplicacao, eixo: EEixoCerne): number | null {
    const p = aplicacao.pontuacoes.find(x => x.dimensao === eixo);
    return p != null ? p.pontuacao : null;
  }

  get concluida(): boolean {
    return this.rodada().situacao === 'CONCLUIDA';
  }

  revisar(aplicacao: Aplicacao): void {
    this.dialog.open(RevisarAplicacaoDialog, {
      width: '90vw',
      maxWidth: '1200px',
      data: { rodCod: this.rodada().rodCod, aplicacao },
    });
  }

  concluir(): void {
    this.concluindo.set(true);
    this.service.concluir(this.rodada().rodCod).subscribe({
      next: () => {
        this.concluindo.set(false);
        this.service.recarregar();
      },
      error: () => this.concluindo.set(false),
    });
  }
}
