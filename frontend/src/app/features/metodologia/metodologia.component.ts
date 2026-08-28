import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import { IndicadoresTabComponent } from './indicadores-tab.component';
import { ProcessosTabComponent } from './processos-tab.component';

/**
 * Tela "Metodologia": abas Processos e Práticas (com as atividades-padrão agrupadas sob cada prática) e
 * Indicadores. Documento vivo — as edições valem na hora (sem versionamento/publicação). O botão
 * "Consolidar metodologia no ciclo em foco" materializa a metodologia no ciclo (estrutura + indicadores
 * + atividades); substitui o que já foi gerado.
 */
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

  /** Ciclo em foco que receberá a materialização (alimenta o rótulo/estado do botão). */
  readonly alvoRes = rxResource({ stream: () => this.service.alvoMaterializacao() });

  readonly materializando = signal(false);
  readonly feedback = signal<string | null>(null);
  readonly erro = signal<string | null>(null);

  materializar(): void {
    const alvo = this.alvoRes.value();
    if (alvo == null) return;
    const ok = window.confirm(
      `Materializar a metodologia no ciclo em foco "${alvo.nome}"? Isto substitui os indicadores e o ` +
        `planejamento gerados do ciclo (os complementares são mantidos).`,
    );
    if (!ok) return;
    this.materializando.set(true);
    this.feedback.set(null);
    this.erro.set(null);
    this.service.materializarMetodologia().subscribe({
      next: () => {
        this.materializando.set(false);
        this.feedback.set(`Metodologia materializada no ciclo "${alvo.nome}".`);
      },
      error: e => {
        this.materializando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Não foi possível materializar a metodologia no ciclo.');
      },
    });
  }
}
