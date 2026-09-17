import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MonitoramentoService } from '../../core/services/monitoramento/monitoramento.service';
import {
  Aplicacao,
  EEixoCerne,
  EIXO_LABEL,
  EIXOS,
  ERecomendacaoMonitor,
  EStatusMonitoramento,
  RECOMENDACAO_LABEL,
  STATUS_MONITORAMENTO_LABEL,
} from '../../models/monitoramento/monitoramento.model';

interface RevisarData {
  rodCod: number;
  aplicacao: Aplicacao;
}

@Component({
  selector: 'app-revisar-aplicacao',
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './revisar-aplicacao.dialog.html',
  styleUrl: './revisar-aplicacao.dialog.css',
})
export class RevisarAplicacaoDialog {
  private readonly service = inject(MonitoramentoService);
  private readonly data = inject<RevisarData>(MAT_DIALOG_DATA);
  private readonly ref = inject(MatDialogRef<RevisarAplicacaoDialog>);

  readonly aplicacao = this.data.aplicacao;

  readonly eixos = EIXOS;
  readonly notasOpcoes = [0, 1, 2, 3, 4, 5];
  readonly recomendacoes = Object.entries(RECOMENDACAO_LABEL) as [ERecomendacaoMonitor, string][];
  readonly statuses = Object.entries(STATUS_MONITORAMENTO_LABEL) as [EStatusMonitoramento, string][];

  readonly notas = signal<Record<EEixoCerne, number | null>>(this.notasIniciais());
  readonly recomendacao = signal<ERecomendacaoMonitor | null>(this.aplicacao.recomendacao);
  readonly observacao = signal(this.aplicacao.observacao ?? '');
  readonly status = signal<EStatusMonitoramento>(this.aplicacao.status ?? 'EM_ANDAMENTO');

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  eixoLabel(e: EEixoCerne): string {
    return EIXO_LABEL[e];
  }

  setNota(eixo: EEixoCerne, valor: number | null): void {
    this.notas.update(atual => ({ ...atual, [eixo]: valor }));
  }

  salvar(): void {
    if (this.salvando()) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<Aplicacao> = {
      status: this.status(),
      recomendacao: this.recomendacao(),
      observacao: this.observacao().trim() || null,
      pontuacoes: this.eixos.map(e => ({ dimensao: e, pontuacao: this.notas()[e] })),
    };
    this.service.revisar(this.data.rodCod, this.aplicacao.empCod, dto).subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao salvar a revisão.');
      },
    });
  }

  private notasIniciais(): Record<EEixoCerne, number | null> {
    const inicial = {} as Record<EEixoCerne, number | null>;
    for (const eixo of EIXOS) {
      const p = this.data.aplicacao.pontuacoes.find(x => x.dimensao === eixo);
      inicial[eixo] = p != null ? p.pontuacao : null;
    }
    return inicial;
  }
}
