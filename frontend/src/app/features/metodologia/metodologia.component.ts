import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { IndicadoresTabComponent } from './indicadores-tab.component';
import { ProcessosTabComponent } from './processos-tab.component';
import { PublicacaoTabComponent } from './publicacao-tab.component';

/**
 * Tela "Metodologia": abas Processos e Práticas, Indicadores e Publicação. Todas editam o rascunho;
 * a aba Publicação materializa uma nova versão publicada.
 */
@Component({
  selector: 'app-metodologia',
  imports: [MatTabsModule, ProcessosTabComponent, IndicadoresTabComponent, PublicacaoTabComponent],
  templateUrl: './metodologia.component.html',
  styleUrl: './metodologia.component.css',
})
export class MetodologiaComponent {}
