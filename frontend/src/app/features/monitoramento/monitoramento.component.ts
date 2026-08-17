import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { AplicacoesTabComponent } from './aplicacoes-tab.component';
import { RodadasTabComponent } from './rodadas-tab.component';

/**
 * Tela "Monitoramento dos empreendimentos": aba "Rodadas" (planeja e lista as rodadas de
 * monitoramento) e aba "Aplicações e pontuação" (avaliação de cada empreendimento por eixo CERNE,
 * com recomendação e status).
 */
@Component({
  selector: 'app-monitoramento',
  imports: [MatTabsModule, RodadasTabComponent, AplicacoesTabComponent],
  templateUrl: './monitoramento.component.html',
  styleUrl: './monitoramento.component.css',
})
export class MonitoramentoComponent {}
