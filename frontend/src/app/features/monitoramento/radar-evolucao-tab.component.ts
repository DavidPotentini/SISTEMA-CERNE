import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { Chart, ChartConfiguration, ChartData, registerables } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

// Registra os controllers do Chart.js no chunk (lazy) do monitoramento — evita carregar o Chart.js
// no bundle inicial (por isso não usamos provideCharts na raiz).
Chart.register(...registerables);
import { EmpreendimentoService } from '../../core/services/empreendimento/empreendimento.service';
import { MonitoramentoService } from '../../core/services/monitoramento/monitoramento.service';
import {
  EEixoCerne,
  EIXO_LABEL,
  EIXOS,
  EvolucaoRodada,
} from '../../models/monitoramento/monitoramento.model';
import { reterRecurso } from '../../shared/util/reter-recurso';

/** Cores por série (rodada), em ordem; ciclam se houver mais rodadas que cores. */
const CORES = [
  '#1565c0',
  '#2e7d32',
  '#ef6c00',
  '#6a1b9a',
  '#c62828',
  '#00838f',
] as const;

/**
 * Aba "Radar de evolução": escolhido um empreendimento, mostra um gráfico de radar com os 5 eixos
 * CERNE e uma série (polígono) por rodada avaliada, de modo a ler a evolução do empreendimento em
 * cada eixo ao longo das rodadas. A lista do filtro traz todos os empreendimentos da incubadora.
 */
@Component({
  selector: 'app-radar-evolucao-tab',
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatProgressBarModule,
    BaseChartDirective,
  ],
  templateUrl: './radar-evolucao-tab.component.html',
  styleUrl: './radar-evolucao-tab.component.css',
})
export class RadarEvolucaoTabComponent {
  private readonly empreendimentoService = inject(EmpreendimentoService);
  private readonly service = inject(MonitoramentoService);

  /** Empreendimento selecionado no filtro. */
  readonly empCod = signal<number | null>(null);

  readonly empreendimentosRes = reterRecurso(rxResource({
    stream: () => this.empreendimentoService.listar(),
  }));

  readonly evolucaoRes = reterRecurso(rxResource({
    params: () => {
      const emp = this.empCod();
      return emp == null ? undefined : { emp, v: this.service.versao() };
    },
    stream: ({ params }) => this.service.evolucao(params.emp),
  }));

  /** Rótulos dos eixos (ordem canônica CERNE). */
  private readonly rotulosEixos = EIXOS.map(e => EIXO_LABEL[e]);

  /** Dados do radar: labels = eixos; um dataset por rodada. */
  readonly chartData = computed<ChartData<'radar', (number | null)[], string>>(() => {
    const serie = this.evolucaoRes.value() ?? [];
    return {
      labels: this.rotulosEixos,
      datasets: serie.map((rodada, i) => {
        const cor = CORES[i % CORES.length];
        return {
          label: rodada.rodadaNome ?? `Rodada ${rodada.rodCod}`,
          data: EIXOS.map(eixo => this.notaDoEixo(rodada, eixo)),
          borderColor: cor,
          pointBackgroundColor: cor,
          backgroundColor: cor + '22',
          fill: true,
          tension: 0,
        };
      }),
    };
  });

  readonly chartOptions: ChartConfiguration<'radar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      r: {
        min: 0,
        max: 5,
        ticks: { stepSize: 1 },
        pointLabels: { font: { size: 13 } },
      },
    },
    plugins: {
      legend: { position: 'bottom' },
    },
  };

  /** true quando há empreendimento escolhido mas nenhuma rodada avaliada. */
  readonly semDados = computed(
    () => this.empCod() != null && !this.evolucaoRes.isLoading() && (this.evolucaoRes.value() ?? []).length === 0,
  );

  private notaDoEixo(rodada: EvolucaoRodada, eixo: EEixoCerne): number | null {
    const p = rodada.pontuacoes.find(pt => pt.dimensao === eixo);
    return p?.pontuacao ?? null;
  }
}
