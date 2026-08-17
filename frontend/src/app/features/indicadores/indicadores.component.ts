import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { IndicadoresCicloTabComponent } from './indicadores-ciclo-tab.component';
import { MetasCicloTabComponent } from './metas-ciclo-tab.component';

/**
 * Tela "Indicadores e metas": aba "Indicadores do ciclo" (define os indicadores do ciclo, gerando da
 * metodologia vigente ou definindo complementares) e aba "Metas do ciclo" (metas por período).
 */
@Component({
  selector: 'app-indicadores',
  imports: [MatTabsModule, IndicadoresCicloTabComponent, MetasCicloTabComponent],
  templateUrl: './indicadores.component.html',
  styleUrl: './indicadores.component.css',
})
export class IndicadoresComponent {}
