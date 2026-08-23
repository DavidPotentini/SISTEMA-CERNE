import { Component, computed, effect, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { ApuracaoService } from '../../core/services/apuracao/apuracao.service';
import {
  ApuracaoIndicador,
  ESituacaoApuracao,
  SITUACAO_APURACAO_LABEL,
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
import { RegistrarResultadosDialog } from './registrar-resultados.dialog';

/**
 * Tela "Apuração de indicadores": mesmo estilo da aba de metas, com a coluna "Apuração"
 * (apurados/total de períodos), a "Situação" (em aberto/atrasada/concluída) e a barra de filtros
 * padrão (Processo/Prática/Responsável/Situação). "Registrar Resultados" abre o modal que lança o
 * resultado de cada período.
 */
@Component({
  selector: 'app-apuracao',
  imports: [
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    FiltrosBarComponent,
  ],
  templateUrl: './apuracao.component.html',
  styleUrl: './apuracao.component.css',
})
export class ApuracaoComponent {
  private readonly service = inject(ApuracaoService);
  private readonly dialog = inject(MatDialog);
  private readonly route = inject(ActivatedRoute);

  /** Deep-link do Painel Operacional já tratado? (evita reabrir quando a lista recarrega). */
  private registroAberto = false;

  readonly colunas = ['nome', 'vinculo', 'periodicidade', 'unidade', 'apuracao', 'situacao', 'acoes'];

  readonly indicadoresRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listar(),
  });

  private readonly todos = computed<ApuracaoIndicador[]>(() => this.indicadoresRes.value() ?? []);

  // ---- filtros padrão ----
  readonly filtros = signal<FiltrosState>({ ...FILTROS_VAZIO });

  readonly statusOpcoes: OpcaoStatus[] = (
    Object.entries(SITUACAO_APURACAO_LABEL) as [ESituacaoApuracao, string][]
  ).map(([value, label]) => ({ value, label }));

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

  /** Listagem já com os filtros aplicados. */
  readonly indicadores = computed<ApuracaoIndicador[]>(() => {
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

  constructor() {
    // Vindo do Painel Operacional (?registrar=indCod): abre "Registrar Resultados" do indicador
    // quando a lista carregar. Só uma vez — recarregar não deve reabrir o modal.
    effect(() => {
      const indicadores = this.indicadoresRes.value();
      if (!indicadores || this.registroAberto) {
        return;
      }
      const indCod = Number(this.route.snapshot.queryParamMap.get('registrar'));
      if (!indCod) {
        return;
      }
      const indicador = indicadores.find(i => i.indCod === indCod);
      if (indicador) {
        this.registroAberto = true;
        this.registrar(indicador);
      }
    });
  }

  periodicidadeLabel(p: EPeriodicidade): string {
    return PERIODICIDADE_LABEL[p];
  }

  situacaoLabel(s: ESituacaoApuracao): string {
    return SITUACAO_APURACAO_LABEL[s];
  }

  registrar(indicador: ApuracaoIndicador): void {
    this.dialog.open(RegistrarResultadosDialog, { width: '720px', data: { indicador } });
  }
}
