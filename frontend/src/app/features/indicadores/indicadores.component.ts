import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { IndicadoresCicloTabComponent } from './indicadores-ciclo-tab.component';
import { MetasCicloTabComponent } from './metas-ciclo-tab.component';
import { PainelTabComponent } from './painel-tab.component';
import { CicloReadonlyBannerComponent } from '../ciclos/ciclo-readonly-banner.component';

@Component({
  selector: 'app-indicadores',
  imports: [
    MatTabsModule,
    IndicadoresCicloTabComponent,
    MetasCicloTabComponent,
    PainelTabComponent,
    CicloReadonlyBannerComponent,
  ],
  templateUrl: './indicadores.component.html',
  styleUrl: './indicadores.component.css',
})
export class IndicadoresComponent {}
