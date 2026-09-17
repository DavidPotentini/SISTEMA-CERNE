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
import { CicloReadonlyBannerComponent } from '../ciclos/ciclo-readonly-banner.component';

/** Circunferência do anel de progresso (r = 52 no viewBox 120×120). */
const CIRCUNFERENCIA = 2 * Math.PI * 52;

@Component({
  selector: 'app-visao-geral',
  imports: [
    RouterLink,
    MatCardModule,
    MatIconModule,
    MatProgressBarModule,
    MatTooltipModule,
    CicloReadonlyBannerComponent,
  ],
  templateUrl: './visao-geral.component.html',
  styleUrl: './visao-geral.component.css',
})
export class VisaoGeralComponent {
  private readonly service = inject(VisaoGeralService);

  readonly circunferencia = CIRCUNFERENCIA;

  readonly resumoRes = rxResource({ stream: () => this.service.resumo() });

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
