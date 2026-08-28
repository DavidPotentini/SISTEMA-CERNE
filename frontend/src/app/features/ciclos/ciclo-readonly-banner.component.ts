import { Component, computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatIconModule } from '@angular/material/icon';
import { CicloService } from '../../core/services/ciclo/ciclo.service';

/**
 * Faixa "somente leitura" das telas que dependem de ciclo: aparece quando o ciclo em foco está
 * ENCERRADO e some sozinha quando o foco é um ciclo ATIVO (ou não há ciclo). Basta incluir
 * `<app-ciclo-readonly-banner />` no topo da tela — reflete a guarda do backend (`@EscopoCiclo`),
 * que responde 409 a qualquer escrita num ciclo encerrado.
 */
@Component({
  selector: 'app-ciclo-readonly-banner',
  imports: [MatIconModule],
  template: `
    @if (cicloEncerrado(); as c) {
      <div class="banner" role="status">
        <mat-icon>history</mat-icon>
        <span>
          Visualizando o ciclo encerrado <strong>{{ c.nome }}</strong> — somente leitura.
          Ponha o ciclo ativo em foco para editar.
        </span>
      </div>
    }
  `,
  styles: `
    .banner {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 16px;
      margin-bottom: 16px;
      border-radius: 8px;
      background: rgba(191, 128, 0, 0.12);
      color: #7a5200;
      border: 1px solid rgba(191, 128, 0, 0.3);
      font-size: 0.9rem;
    }
    .banner mat-icon {
      flex: 0 0 auto;
    }
  `,
})
export class CicloReadonlyBannerComponent {
  private readonly service = inject(CicloService);

  private readonly ciclosRes = rxResource({
    params: () => this.service.versao(),
    stream: () => this.service.listar(),
  });

  /** Ciclo em foco quando está ENCERRADO, ou {@code null} (foco ativo / sem ciclo). */
  readonly cicloEncerrado = computed(
    () => this.ciclosRes.value()?.find(c => c.emFoco && c.status === 'ENCERRADO') ?? null,
  );
}
