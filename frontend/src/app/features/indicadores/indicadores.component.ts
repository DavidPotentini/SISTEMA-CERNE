import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { IndicadoresCicloTabComponent } from './indicadores-ciclo-tab.component';
import { MetasCicloTabComponent } from './metas-ciclo-tab.component';
import { PainelTabComponent } from './painel-tab.component';

/**
 * Tela "Indicadores e metas": aba "Indicadores do ciclo" (define os indicadores do ciclo, gerando da
 * metodologia vigente ou definindo complementares), aba "Metas do ciclo" (metas por período) e aba
 * "Painel" (consolidação do ciclo: cards, listagem por indicador e atingidos por processo CERNE).
 */
@Component({
  selector: 'app-indicadores',
  imports: [MatTabsModule, IndicadoresCicloTabComponent, MetasCicloTabComponent, PainelTabComponent],
  templateUrl: './indicadores.component.html',
  styleUrl: './indicadores.component.css',
})
export class IndicadoresComponent {}
