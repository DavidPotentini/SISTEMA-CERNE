import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { AplicacoesTabComponent } from './aplicacoes-tab.component';
import { RadarEvolucaoTabComponent } from './radar-evolucao-tab.component';
import { RodadasTabComponent } from './rodadas-tab.component';

/**
 * Tela "Monitoramento dos empreendimentos": aba "Rodadas" (planeja e lista as rodadas de
 * monitoramento), aba "Aplicações e pontuação" (avaliação de cada empreendimento por eixo CERNE,
 * com recomendação e status) e aba "Radar de evolução" (gráfico de radar da evolução de um
 * empreendimento por eixo ao longo das rodadas).
 */
@Component({
  selector: 'app-monitoramento',
  imports: [MatTabsModule, RodadasTabComponent, AplicacoesTabComponent, RadarEvolucaoTabComponent],
  templateUrl: './monitoramento.component.html',
  styleUrl: './monitoramento.component.css',
})
export class MonitoramentoComponent {}
