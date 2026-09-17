import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import {
  EPeriodicidade,
  Indicador,
  PERIODICIDADE_LABEL,
} from '../../models/metodologia/metodologia.model';
import { IndicadorFormDialog } from './indicador-form.dialog';
import { reterRecurso } from '../../shared/util/reter-recurso';

@Component({
  selector: 'app-indicadores-tab',
  imports: [
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatDialogModule,
  ],
  templateUrl: './indicadores-tab.component.html',
  styleUrl: './indicadores-tab.component.css',
})
export class IndicadoresTabComponent {
  private readonly service = inject(MetodologiaService);
  private readonly dialog = inject(MatDialog);

  readonly colunas = ['nome', 'vinculo', 'unidade', 'periodicidade', 'situacao', 'acoes'];

  readonly indicadores = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listarIndicadores(),
  }));

  periodicidadeLabel(p: EPeriodicidade): string {
    return PERIODICIDADE_LABEL[p];
  }

  adicionar(): void {
    this.dialog.open(IndicadorFormDialog, { width: '90vw', maxWidth: '1200px' });
  }

  editar(i: Indicador): void {
    this.dialog.open(IndicadorFormDialog, { width: '90vw', maxWidth: '1200px', data: { indicador: i } });
  }

  alternar(i: Indicador): void {
    const situacao = i.situacao === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    this.service.alterarSituacaoIndicador(i.inmCod, situacao).subscribe(() => this.service.recarregar());
  }
}
