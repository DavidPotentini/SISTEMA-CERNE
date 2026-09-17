import { Component, computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatIconModule } from '@angular/material/icon';
import { CicloService } from '../../core/services/ciclo/ciclo.service';
import { reterRecurso } from '../../shared/util/reter-recurso';

@Component({
  selector: 'app-ciclo-readonly-banner',
  imports: [MatIconModule],
  template: `
    <div class="ciclo-em-foco">
      @if (cicloEmFoco(); as c) {
        <span class="chip" [class.encerrado]="c.status === 'ENCERRADO'">
          <mat-icon>{{ c.status === 'ENCERRADO' ? 'history' : 'adjust' }}</mat-icon>
          <span class="rot">Ciclo em foco:</span>
          <strong>{{ c.nome }}</strong>
        </span>
      } @else {
        <span class="chip vazio">
          <mat-icon>info</mat-icon>
          Nenhum ciclo em foco
        </span>
      }
    </div>

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
    .ciclo-em-foco {
      display: flex;
      justify-content: flex-end;
      margin-bottom: 8px;
    }
    .chip {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 5px 12px;
      border-radius: 999px;
      font-size: 0.82rem;
      background: rgba(21, 101, 192, 0.1);
      color: #1565c0;
      border: 1px solid rgba(21, 101, 192, 0.25);
    }
    .chip .rot {
      color: rgba(0, 0, 0, 0.55);
    }
    .chip mat-icon {
      font-size: 16px;
      width: 16px;
      height: 16px;
    }
    .chip.encerrado {
      background: rgba(191, 128, 0, 0.12);
      color: #7a5200;
      border-color: rgba(191, 128, 0, 0.3);
    }
    .chip.encerrado .rot {
      color: rgba(122, 82, 0, 0.7);
    }
    .chip.vazio {
      background: rgba(0, 0, 0, 0.05);
      color: rgba(0, 0, 0, 0.6);
      border-color: rgba(0, 0, 0, 0.12);
    }
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

  private readonly ciclosRes = reterRecurso(rxResource({
    params: () => this.service.versao(),
    stream: () => this.service.listar(),
  }));

  readonly cicloEmFoco = computed(() => {
    const ciclos = this.ciclosRes.value() ?? [];
    return ciclos.find(c => c.emFoco) ?? ciclos.find(c => c.status === 'ATIVO') ?? null;
  });

  readonly cicloEncerrado = computed(
    () => this.ciclosRes.value()?.find(c => c.emFoco && c.status === 'ENCERRADO') ?? null,
  );
}
