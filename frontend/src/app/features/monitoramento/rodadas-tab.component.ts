import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { MonitoramentoService } from '../../core/services/monitoramento/monitoramento.service';
import {
  ESituacaoRodada,
  ETipoRodada,
  SITUACAO_RODADA_LABEL,
  TIPO_RODADA_LABEL,
} from '../../models/monitoramento/monitoramento.model';
import { PlanejarRodadaDialog } from './planejar-rodada.dialog';

/**
 * Aba "Rodadas": botão "Planejar Rodada" (abre o modal) e a listagem de todas as rodadas
 * (nome, tipo, responsável, prazo e situação).
 */
@Component({
  selector: 'app-rodadas-tab',
  imports: [
    DatePipe,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatDialogModule,
  ],
  templateUrl: './rodadas-tab.component.html',
  styleUrl: './rodadas-tab.component.css',
})
export class RodadasTabComponent {
  private readonly service = inject(MonitoramentoService);
  private readonly dialog = inject(MatDialog);

  readonly colunas = ['nome', 'tipo', 'responsavel', 'prazo', 'situacao'];

  readonly rodadasRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listarRodadas(),
  });

  tipoLabel(t: ETipoRodada): string {
    return TIPO_RODADA_LABEL[t];
  }

  situacaoLabel(s: ESituacaoRodada): string {
    return SITUACAO_RODADA_LABEL[s];
  }

  planejar(): void {
    this.dialog.open(PlanejarRodadaDialog, { width: '640px' });
  }
}
