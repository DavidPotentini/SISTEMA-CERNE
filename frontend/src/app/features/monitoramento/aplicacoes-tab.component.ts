import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MonitoramentoService } from '../../core/services/monitoramento/monitoramento.service';
import { RodadaAplicacoesComponent } from './rodada-aplicacoes.component';

/**
 * Aba "Aplicações e pontuação": lista as rodadas e, para cada uma, delega ao
 * {@link RodadaAplicacoesComponent} a exibição dos seus empreendimentos (cards com pontuação por
 * eixo, recomendação e status) — cada rodada carrega seus cards do próprio endpoint.
 */
@Component({
  selector: 'app-aplicacoes-tab',
  imports: [MatProgressBarModule, RodadaAplicacoesComponent],
  templateUrl: './aplicacoes-tab.component.html',
  styleUrl: './aplicacoes-tab.component.css',
})
export class AplicacoesTabComponent {
  private readonly service = inject(MonitoramentoService);

  readonly rodadasRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listarRodadas(),
  });
}
