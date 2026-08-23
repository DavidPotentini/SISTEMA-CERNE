import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { IndicadoresTabComponent } from './indicadores-tab.component';
import { ProcessosTabComponent } from './processos-tab.component';

/**
 * Tela "Metodologia": abas Processos e Práticas e Indicadores. Documento vivo — as edições valem na
 * hora (sem versionamento/publicação).
 */
@Component({
  selector: 'app-metodologia',
  imports: [MatTabsModule, ProcessosTabComponent, IndicadoresTabComponent],
  templateUrl: './metodologia.component.html',
  styleUrl: './metodologia.component.css',
})
export class MetodologiaComponent {}
