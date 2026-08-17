import { Component, computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { of } from 'rxjs';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import {
  EPeriodicidade,
  Indicador,
  PERIODICIDADE_LABEL,
} from '../../models/metodologia/metodologia.model';
import { IndicadorFormDialog } from './indicador-form.dialog';

/**
 * Aba "Indicadores": lista os indicadores da versão vigente (nome, vínculo metodológico, unidade,
 * periodicidade), com editar e ativar/inativar por item, e "Adicionar Indicador" no topo direito.
 */
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

  readonly versaoRes = rxResource({ stream: () => this.service.versaoDeTrabalho() });
  readonly verCod = computed(() => this.versaoRes.value()?.verCod ?? null);

  readonly indicadores = rxResource({
    params: () => ({ verCod: this.verCod(), v: this.service.versao() }),
    stream: ({ params }) =>
      params.verCod == null ? of<Indicador[]>([]) : this.service.listarIndicadores(params.verCod),
  });

  periodicidadeLabel(p: EPeriodicidade): string {
    return PERIODICIDADE_LABEL[p];
  }

  adicionar(): void {
    const verCod = this.verCod();
    if (verCod == null) return;
    this.dialog.open(IndicadorFormDialog, { width: '560px', data: { verCod } });
  }

  editar(i: Indicador): void {
    const verCod = this.verCod();
    if (verCod == null) return;
    this.dialog.open(IndicadorFormDialog, { width: '560px', data: { verCod, indicador: i } });
  }

  alternar(i: Indicador): void {
    const situacao = i.situacao === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    this.service.alterarSituacaoIndicador(i.inmCod, situacao).subscribe(() => this.service.recarregar());
  }
}
