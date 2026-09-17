import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { AplicacoesTabComponent } from './aplicacoes-tab.component';
import { RadarEvolucaoTabComponent } from './radar-evolucao-tab.component';
import { RodadasTabComponent } from './rodadas-tab.component';
import { CicloReadonlyBannerComponent } from '../ciclos/ciclo-readonly-banner.component';

@Component({
  selector: 'app-monitoramento',
  imports: [
    MatTabsModule,
    RodadasTabComponent,
    AplicacoesTabComponent,
    RadarEvolucaoTabComponent,
    CicloReadonlyBannerComponent,
  ],
  templateUrl: './monitoramento.component.html',
  styleUrl: './monitoramento.component.css',
})
export class MonitoramentoComponent {}
