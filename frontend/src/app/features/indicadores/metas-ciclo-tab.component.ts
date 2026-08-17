import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { IndicadorService } from '../../core/services/indicador/indicador.service';
import { IndicadorCiclo } from '../../models/indicador/indicador.model';
import {
  EPeriodicidade,
  PERIODICIDADE_LABEL,
} from '../../models/metodologia/metodologia.model';
import { MetaIndicadorDialog } from './meta-indicador.dialog';

/**
 * Aba "Metas do ciclo": lista os indicadores do ciclo (nome, vínculo CERNE, periodicidade, unidade)
 * com o botão de meta ao lado — abre o modal para cadastrar/visualizar os períodos (meta estipulada +
 * janela de apuração) do indicador.
 */
@Component({
  selector: 'app-metas-ciclo-tab',
  imports: [MatCardModule, MatTableModule, MatButtonModule, MatIconModule, MatDialogModule],
  templateUrl: './metas-ciclo-tab.component.html',
  styleUrl: './metas-ciclo-tab.component.css',
})
export class MetasCicloTabComponent {
  private readonly service = inject(IndicadorService);
  private readonly dialog = inject(MatDialog);

  readonly colunas = ['nome', 'vinculo', 'periodicidade', 'unidade', 'acoes'];

  readonly indicadoresRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listar(),
  });

  periodicidadeLabel(p: EPeriodicidade): string {
    return PERIODICIDADE_LABEL[p];
  }

  abrirMeta(indicador: IndicadorCiclo): void {
    this.dialog.open(MetaIndicadorDialog, { width: '680px', data: { indicador } });
  }
}
