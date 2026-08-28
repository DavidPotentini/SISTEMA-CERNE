import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MonitoramentoService } from '../../core/services/monitoramento/monitoramento.service';
import { RodadaAplicacoesComponent } from './rodada-aplicacoes.component';

/**
 * Aba "Aplicações e pontuação": barra de filtro (nome do empreendimento / status) e a lista de
 * rodadas. Cada rodada ({@link RodadaAplicacoesComponent}) é uma seção recolhível com a tabela densa
 * dos seus empreendimentos, recebendo o filtro para aplicar e se expandir quando houver resultados.
 */
@Component({
  selector: 'app-aplicacoes-tab',
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatProgressBarModule,
    RodadaAplicacoesComponent,
  ],
  templateUrl: './aplicacoes-tab.component.html',
  styleUrl: './aplicacoes-tab.component.css',
})
export class AplicacoesTabComponent {
  private readonly service = inject(MonitoramentoService);

  readonly filtroNome = signal('');
  readonly filtroStatus = signal('TODOS');

  readonly rodadasRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listarRodadas(),
  });
}
