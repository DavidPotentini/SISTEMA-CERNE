import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { IndicadorService } from '../../core/services/indicador/indicador.service';
import {
  EOrigemIndicador,
  ORIGEM_INDICADOR_LABEL,
} from '../../models/indicador/indicador.model';
import {
  EPeriodicidade,
  PERIODICIDADE_LABEL,
} from '../../models/metodologia/metodologia.model';
import { IndicadorComplementarDialog } from './indicador-complementar.dialog';

/**
 * Aba "Indicadores do ciclo": lista os indicadores do ciclo ativo (nome, origem, vínculo CERNE,
 * unidade, periodicidade, situação). "Gerar indicadores do ciclo" copia da metodologia vigente;
 * "Definir complementar" inclui um indicador manual.
 */
@Component({
  selector: 'app-indicadores-ciclo-tab',
  imports: [
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatDialogModule,
  ],
  templateUrl: './indicadores-ciclo-tab.component.html',
  styleUrl: './indicadores-ciclo-tab.component.css',
})
export class IndicadoresCicloTabComponent {
  private readonly service = inject(IndicadorService);
  private readonly dialog = inject(MatDialog);

  readonly colunas = ['nome', 'origem', 'vinculo', 'unidade', 'periodicidade', 'situacao'];

  readonly gerando = signal(false);
  readonly erroAcao = signal<string | null>(null);

  readonly indicadoresRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listar(),
  });

  rotuloOrigem(origem: EOrigemIndicador): string {
    return ORIGEM_INDICADOR_LABEL[origem];
  }

  periodicidadeLabel(p: EPeriodicidade): string {
    return PERIODICIDADE_LABEL[p];
  }

  gerar(): void {
    this.gerando.set(true);
    this.erroAcao.set(null);
    this.service.gerar().subscribe({
      next: () => {
        this.gerando.set(false);
        this.service.recarregar();
      },
      error: e => {
        this.gerando.set(false);
        this.erroAcao.set(e?.error?.mensagem ?? 'Não foi possível gerar os indicadores do ciclo.');
      },
    });
  }

  definirComplementar(): void {
    this.dialog.open(IndicadorComplementarDialog, { width: '560px' });
  }
}
