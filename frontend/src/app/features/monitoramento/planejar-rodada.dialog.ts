import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { EmpreendimentoService } from '../../core/services/empreendimento/empreendimento.service';
import { MonitoramentoService } from '../../core/services/monitoramento/monitoramento.service';
import {
  ETipoRodada,
  Rodada,
  TIPO_RODADA_LABEL,
} from '../../models/monitoramento/monitoramento.model';

/**
 * Modal "Planejar Rodada": informa os dados da rodada (nome, tipo, responsável, prazo) e escolhe os
 * empreendimentos participantes. O ciclo é o ativo (definido no backend). Empreendimentos e
 * responsáveis (equipe da incubadora) vêm dos endpoints de empreendimentos.
 */
@Component({
  selector: 'app-planejar-rodada',
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './planejar-rodada.dialog.html',
  styleUrl: './planejar-rodada.dialog.css',
})
export class PlanejarRodadaDialog {
  private readonly service = inject(MonitoramentoService);
  private readonly empreendimentoService = inject(EmpreendimentoService);
  private readonly ref = inject(MatDialogRef<PlanejarRodadaDialog>);

  readonly tipos = Object.entries(TIPO_RODADA_LABEL) as [ETipoRodada, string][];

  readonly empreendimentosRes = rxResource({
    stream: () => this.empreendimentoService.listar(),
  });
  readonly responsaveisRes = rxResource({
    stream: () => this.empreendimentoService.listarResponsaveis(),
  });

  readonly nome = signal('');
  readonly tipo = signal<ETipoRodada>('PERIODICO');
  readonly respPesCod = signal<number | null>(null);
  readonly prazo = signal<string | null>(null);
  readonly empCods = signal<number[]>([]);

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  get podeSalvar(): boolean {
    return this.nome().trim().length > 0 && !this.salvando();
  }

  salvar(): void {
    if (!this.podeSalvar) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<Rodada> = {
      nome: this.nome().trim(),
      tipo: this.tipo(),
      respPesCod: this.respPesCod(),
      prazo: this.prazo(),
      empCods: this.empCods(),
    };
    this.service.planejar(dto).subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao planejar a rodada.');
      },
    });
  }
}
