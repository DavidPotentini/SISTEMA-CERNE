import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import { GerarCicloDialog } from './gerar-ciclo.dialog';
import { IndicadoresTabComponent } from './indicadores-tab.component';
import { ProcessosTabComponent } from './processos-tab.component';

@Component({
  selector: 'app-metodologia',
  imports: [
    MatTabsModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatTooltipModule,
    ProcessosTabComponent,
    IndicadoresTabComponent,
  ],
  templateUrl: './metodologia.component.html',
  styleUrl: './metodologia.component.css',
})
export class MetodologiaComponent {
  private readonly service = inject(MetodologiaService);
  private readonly dialog = inject(MatDialog);

  readonly alvoRes = rxResource({ stream: () => this.service.alvoMaterializacao() });

  readonly materializando = signal(false);
  readonly feedback = signal<string | null>(null);
  readonly erro = signal<string | null>(null);

  materializar(): void {
    const alvo = this.alvoRes.value();
    if (alvo == null) return;
    this.dialog
      .open(GerarCicloDialog, { width: '90vw', maxWidth: '560px', data: { ciclo: alvo.nome } })
      .afterClosed()
      .subscribe((empCods: number[] | undefined) => {
        if (empCods == null) return;
        this.gerar(alvo.nome, empCods);
      });
  }

  private gerar(cicloNome: string, empCods: number[]): void {
    this.materializando.set(true);
    this.feedback.set(null);
    this.erro.set(null);
    this.service.materializarMetodologia(empCods).subscribe({
      next: () => {
        this.materializando.set(false);
        this.feedback.set(`Metodologia publicada no ciclo "${cicloNome}".`);
      },
      error: e => {
        this.materializando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Não foi possível materializar a metodologia no ciclo.');
      },
    });
  }
}
