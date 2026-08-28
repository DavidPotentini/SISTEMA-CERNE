import { Component, computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { VisaoGeralService } from '../../core/services/painel/visao-geral.service';
import {
  EEstadoProcesso,
  ESTADO_PROCESSO_LABEL,
} from '../../models/painel/visao-geral.model';

/** Circunferência do anel de progresso (r = 52 no viewBox 120×120). */
const CIRCUNFERENCIA = 2 * Math.PI * 52;

/**
 * Tela "Visão geral": resumo de andamento do ciclo em foco. Um anel com o % de atividades concluídas,
 * cards de contagem (empreendimentos ativos, evidências registradas/validadas, indicadores com meta
 * atingida) e o fluxo de processos com cada nó colorido pelo estado (concluído/em andamento/não
 * iniciado). Cada bloco navega para a tela de origem.
 */
@Component({
  selector: 'app-visao-geral',
  imports: [
    RouterLink,
    MatCardModule,
    MatIconModule,
    MatProgressBarModule,
    MatTooltipModule,
  ],
  templateUrl: './visao-geral.component.html',
  styleUrl: './visao-geral.component.css',
})
export class VisaoGeralComponent {
  private readonly service = inject(VisaoGeralService);

  readonly circunferencia = CIRCUNFERENCIA;

  readonly resumoRes = rxResource({ stream: () => this.service.resumo() });

  /** Deslocamento do traço do anel conforme o progresso (0% = vazio, 100% = cheio). */
  readonly offset = computed<number>(() => {
    const p = this.resumoRes.value()?.progresso ?? 0;
    return CIRCUNFERENCIA * (1 - p / 100);
  });

  estadoLabel(e: EEstadoProcesso): string {
    return ESTADO_PROCESSO_LABEL[e];
  }

  estadoClasse(e: EEstadoProcesso): string {
    if (e === 'CONCLUIDO') return 'concluido';
    if (e === 'EM_ANDAMENTO') return 'andamento';
    return 'nao-iniciado';
  }
}
