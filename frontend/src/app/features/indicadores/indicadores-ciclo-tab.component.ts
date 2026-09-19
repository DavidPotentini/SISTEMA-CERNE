import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { EquipeService } from '../../core/services/equipe/equipe.service';
import { IndicadorService } from '../../core/services/indicador/indicador.service';
import {
  EOrigemIndicador,
  IndicadorCiclo,
  ORIGEM_INDICADOR_LABEL,
} from '../../models/indicador/indicador.model';
import {
  EPeriodicidade,
  PERIODICIDADE_LABEL,
} from '../../models/metodologia/metodologia.model';
import {
  FILTROS_VAZIO,
  FiltrosState,
  FiltrosBarComponent,
  OpcaoPratica,
  OpcaoProcesso,
  OpcaoStatus,
  casaFiltros,
} from '../../shared/ui/filtros-bar/filtros-bar.component';
import { IndicadorComplementarDialog } from './indicador-complementar.dialog';
import { reterRecurso } from '../../shared/util/reter-recurso';

@Component({
  selector: 'app-indicadores-ciclo-tab',
  imports: [
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatSelectModule,
    MatDialogModule,
    FiltrosBarComponent,
  ],
  templateUrl: './indicadores-ciclo-tab.component.html',
  styleUrl: './indicadores-ciclo-tab.component.css',
})
export class IndicadoresCicloTabComponent {
  private readonly service = inject(IndicadorService);
  private readonly equipeService = inject(EquipeService);
  private readonly dialog = inject(MatDialog);

  readonly colunas = ['nome', 'origem', 'vinculo', 'unidade', 'periodicidade', 'responsavel', 'situacao', 'acoes'];

  readonly erroAcao = signal<string | null>(null);

  readonly indicadoresRes = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listar(),
  }));

  readonly todos = computed<IndicadorCiclo[]>(() => this.indicadoresRes.value() ?? []);

  readonly responsaveisRes = reterRecurso(rxResource({
    stream: () => this.equipeService.listarResponsaveis(),
  }));

  readonly filtros = signal<FiltrosState>({ ...FILTROS_VAZIO });

  readonly statusOpcoes: OpcaoStatus[] = [
    { value: 'ATIVO', label: 'Ativo' },
    { value: 'INATIVO', label: 'Inativo' },
  ];

  readonly processoOpcoes = computed<OpcaoProcesso[]>(() => {
    const nomes = new Set<string>();
    for (const i of this.todos()) {
      if (i.processoNome) nomes.add(i.processoNome);
    }
    return [...nomes].map(nome => ({ nome }));
  });

  readonly praticaOpcoes = computed<OpcaoPratica[]>(() => {
    const mapa = new Map<string, OpcaoPratica>();
    for (const i of this.todos()) {
      if (i.processoNome && i.praticaNome && !mapa.has(i.praticaNome)) {
        mapa.set(i.praticaNome, { nome: i.praticaNome, processoNome: i.processoNome });
      }
    }
    return [...mapa.values()];
  });

  readonly indicadores = computed<IndicadorCiclo[]>(() => {
    const f = this.filtros();
    return this.todos().filter(i =>
      casaFiltros(f, {
        processoNome: i.processoNome,
        praticaNome: i.praticaNome,
        respPesCod: i.respPesCod,
        status: i.situacao,
      }),
    );
  });

  rotuloOrigem(origem: EOrigemIndicador): string {
    return ORIGEM_INDICADOR_LABEL[origem];
  }

  periodicidadeLabel(p: EPeriodicidade): string {
    return PERIODICIDADE_LABEL[p];
  }

  editar(indicador: IndicadorCiclo): void {
    this.dialog.open(IndicadorComplementarDialog, {
      data: indicador,
      width: '90vw',
      maxWidth: '1200px',
    });
  }

  definirComplementar(): void {
    this.dialog.open(IndicadorComplementarDialog, { width: '90vw', maxWidth: '1200px' });
  }
}
