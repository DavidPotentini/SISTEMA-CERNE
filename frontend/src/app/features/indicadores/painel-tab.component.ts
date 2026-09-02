import { DecimalPipe } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ApuracaoService } from '../../core/services/apuracao/apuracao.service';
import { PainelIndicador } from '../../models/indicador/indicador.model';
import { EPeriodicidade, PERIODICIDADE_LABEL } from '../../models/metodologia/metodologia.model';
import { reterRecurso } from '../../shared/util/reter-recurso';

/** Consolidação de "atingidos / total" por processo CERNE (denominador = todos os indicadores do processo). */
interface ConsolidacaoProcesso {
  processo: string;
  total: number;
  atingidos: number;
  percentual: number;
}

/**
 * Aba "Painel": cards do ciclo (total, com meta, com resultados pendentes), listagem por indicador
 * (meta e atingido somados de todos os períodos + %) e a consolidação de atingidos por processo CERNE.
 * Tudo derivado da lista do endpoint /apuracao/painel.
 */
@Component({
  selector: 'app-painel-tab',
  imports: [DecimalPipe, MatProgressBarModule],
  templateUrl: './painel-tab.component.html',
  styleUrl: './painel-tab.component.css',
})
export class PainelTabComponent {
  private readonly service = inject(ApuracaoService);

  readonly painelRes = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.painel(),
  }));

  readonly indicadores = computed<PainelIndicador[]>(() => this.painelRes.value() ?? []);

  // cards
  readonly total = computed(() => this.indicadores().length);
  readonly comMeta = computed(() => this.indicadores().filter(i => i.temMeta).length);
  readonly pendentes = computed(() => this.indicadores().filter(i => i.pendente).length);

  // consolidação por processo CERNE
  readonly consolidacao = computed<ConsolidacaoProcesso[]>(() => {
    const mapa = new Map<string, { total: number; atingidos: number }>();
    for (const i of this.indicadores()) {
      const chave = i.processoNome ?? 'Sem processo';
      const acc = mapa.get(chave) ?? { total: 0, atingidos: 0 };
      acc.total++;
      if (i.atingido) acc.atingidos++;
      mapa.set(chave, acc);
    }
    return [...mapa.entries()].map(([processo, v]) => ({
      processo,
      total: v.total,
      atingidos: v.atingidos,
      percentual: v.total > 0 ? (v.atingidos / v.total) * 100 : 0,
    }));
  });

  periodicidadeLabel(p: EPeriodicidade): string {
    return PERIODICIDADE_LABEL[p];
  }
}
