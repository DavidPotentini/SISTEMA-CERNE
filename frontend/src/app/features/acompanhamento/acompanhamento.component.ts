import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { PlanejamentoService } from '../../core/services/planejamento/planejamento.service';
import {
  AtividadePlanejada,
  EStatusAtividade,
  PlanProcesso,
  STATUS_ATIVIDADE_LABEL,
  STATUS_PLANEJAMENTO_LABEL,
} from '../../models/planejamento/planejamento.model';
import { AtividadeRegistroDialog } from './atividade-registro.dialog';

/** Estados que podem ser filtrados/definidos manualmente (ATRASADA é derivado do prazo). */
const STATUS_FILTRAVEIS: EStatusAtividade[] = ['PLANEJADA', 'EM_ANDAMENTO', 'CONCLUIDA', 'ATRASADA'];

/**
 * Tela "Acompanhamento de execução": mesma estrutura do planejamento (cabeçalho + accordion de
 * processos/práticas/atividades), porém só leitura da estrutura. Cada processo mostra o total de
 * atividades e quantas estão concluídas; um filtro por status recorta as atividades exibidas; o botão
 * "Registrar" de cada atividade abre o modal para mudar o status e avaliar as evidências.
 */
@Component({
  selector: 'app-acompanhamento',
  imports: [
    DatePipe,
    MatCardModule,
    MatExpansionModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressBarModule,
    MatDialogModule,
  ],
  templateUrl: './acompanhamento.component.html',
  styleUrl: './acompanhamento.component.css',
})
export class AcompanhamentoComponent {
  private readonly service = inject(PlanejamentoService);
  private readonly dialog = inject(MatDialog);

  readonly statusPlanoLabel = STATUS_PLANEJAMENTO_LABEL;
  readonly statusLabel = STATUS_ATIVIDADE_LABEL;
  readonly statusFiltraveis = STATUS_FILTRAVEIS;

  /** Status selecionado no filtro (null = todas). */
  readonly filtro = signal<EStatusAtividade | null>(null);

  readonly atualRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.atual(),
  });
  readonly estruturaRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.estrutura(),
  });

  readonly plano = computed(() => this.atualRes.value()?.planejamento ?? null);

  filtrar(status: EStatusAtividade): void {
    this.filtro.set(this.filtro() === status ? null : status);
  }

  /** Atividades da prática já aplicado o filtro de status. */
  visiveis(atividades: AtividadePlanejada[]): AtividadePlanejada[] {
    const status = this.filtro();
    return status ? atividades.filter(a => a.status === status) : atividades;
  }

  private atividadesDe(proc: PlanProcesso): AtividadePlanejada[] {
    return proc.praticas.flatMap(pr => pr.atividades);
  }

  /** Total de atividades do processo (independente do filtro). */
  total(proc: PlanProcesso): number {
    return this.atividadesDe(proc).length;
  }

  /** Atividades concluídas do processo (numerador exibido no cabeçalho). */
  concluidas(proc: PlanProcesso): number {
    return this.atividadesDe(proc).filter(a => a.status === 'CONCLUIDA').length;
  }

  registrar(atv: AtividadePlanejada): void {
    this.dialog.open(AtividadeRegistroDialog, {
      width: '640px',
      data: { atividade: atv },
    });
  }
}
