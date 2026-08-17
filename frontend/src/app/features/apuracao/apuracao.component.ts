import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { ApuracaoService } from '../../core/services/apuracao/apuracao.service';
import { ApuracaoIndicador } from '../../models/indicador/indicador.model';
import {
  EPeriodicidade,
  PERIODICIDADE_LABEL,
} from '../../models/metodologia/metodologia.model';
import { RegistrarResultadosDialog } from './registrar-resultados.dialog';

/**
 * Tela "Apuração de indicadores": mesmo estilo da aba de metas, com a coluna "Apuração"
 * (apurados/total de períodos, ou "sem meta"). "Registrar Resultados" abre o modal que lança o
 * resultado de cada período.
 */
@Component({
  selector: 'app-apuracao',
  imports: [MatCardModule, MatTableModule, MatButtonModule, MatIconModule, MatDialogModule],
  templateUrl: './apuracao.component.html',
  styleUrl: './apuracao.component.css',
})
export class ApuracaoComponent {
  private readonly service = inject(ApuracaoService);
  private readonly dialog = inject(MatDialog);

  readonly colunas = ['nome', 'vinculo', 'periodicidade', 'unidade', 'apuracao', 'acoes'];

  readonly indicadoresRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listar(),
  });

  periodicidadeLabel(p: EPeriodicidade): string {
    return PERIODICIDADE_LABEL[p];
  }

  registrar(indicador: ApuracaoIndicador): void {
    this.dialog.open(RegistrarResultadosDialog, { width: '720px', data: { indicador } });
  }
}
