import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ArquivoService } from '../../core/services/arquivo/arquivo.service';
import { EvidenciaService } from '../../core/services/evidencia/evidencia.service';
import { ExecucaoService } from '../../core/services/execucao/execucao.service';
import { PlanejamentoService } from '../../core/services/planejamento/planejamento.service';
import {
  EStatusEvidencia,
  Evidencia,
  STATUS_EVIDENCIA_LABEL,
} from '../../models/evidencia/evidencia.model';
import {
  AtividadePlanejada,
  EStatusAtividade,
  STATUS_ATIVIDADE_LABEL,
} from '../../models/planejamento/planejamento.model';

interface AtividadeRegistroData {
  atividade: AtividadePlanejada;
}

const STATUS_ICONE: Record<EStatusAtividade, string> = {
  PLANEJADA: 'schedule',
  EM_ANDAMENTO: 'autorenew',
  CONCLUIDA: 'check_circle',
  ATRASADA: 'error',
};

@Component({
  selector: 'app-atividade-registro',
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressBarModule,
  ],
  templateUrl: './atividade-registro.dialog.html',
  styleUrl: './atividade-registro.dialog.css',
})
export class AtividadeRegistroDialog {
  private readonly execucao = inject(ExecucaoService);
  private readonly evidencias = inject(EvidenciaService);
  private readonly planejamento = inject(PlanejamentoService);
  private readonly arquivoService = inject(ArquivoService);
  private readonly data = inject<AtividadeRegistroData>(MAT_DIALOG_DATA);

  readonly atividade = signal<AtividadePlanejada>(this.data.atividade);
  readonly statusIcone = STATUS_ICONE;
  readonly statusAtividadeLabel = STATUS_ATIVIDADE_LABEL;
  readonly statusEvidenciaLabel = STATUS_EVIDENCIA_LABEL;

  readonly concluida = computed(() => this.atividade().status === 'CONCLUIDA');

  readonly salvandoStatus = signal(false);
  readonly erroStatus = signal<string | null>(null);
  readonly statusSalvo = signal(false);

  readonly carregando = signal(true);
  readonly lista = signal<Evidencia[]>([]);
  readonly erro = signal<string | null>(null);
  readonly abrindoArquivo = signal(false);
  readonly avaliandoCod = signal<number | null>(null);
  readonly motivos = signal<Record<number, string>>({});

  readonly todasValidadas = computed(() => {
    const evs = this.lista();
    return evs.length > 0 && evs.every(e => e.status === 'VALIDADA');
  });

  constructor() {
    this.carregarEvidencias();
  }

  private carregarEvidencias(): void {
    this.carregando.set(true);
    this.evidencias.listar().subscribe({
      next: todas => {
        this.lista.set(todas.filter(e => e.atpCod === this.data.atividade.atpCod));
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar as evidências da atividade.');
        this.carregando.set(false);
      },
    });
  }

  concluir(): void {
    this.mudarStatus('CONCLUIDA');
  }

  reabrir(): void {
    this.mudarStatus('EM_ANDAMENTO');
  }

  private mudarStatus(status: EStatusAtividade): void {
    this.salvandoStatus.set(true);
    this.erroStatus.set(null);
    this.statusSalvo.set(false);
    this.execucao.mudarStatus(this.data.atividade.atpCod, status).subscribe({
      next: () => {
        this.atividade.update(a => ({ ...a, status }));
        this.salvandoStatus.set(false);
        this.statusSalvo.set(true);
        this.planejamento.recarregar();
      },
      error: e => {
        this.salvandoStatus.set(false);
        this.erroStatus.set(e?.error?.mensagem ?? 'Não foi possível mudar o status da atividade.');
      },
    });
  }

  motivoDe(evdCod: number): string {
    return this.motivos()[evdCod] ?? '';
  }

  atualizarMotivo(evdCod: number, texto: string): void {
    this.motivos.update(o => ({ ...o, [evdCod]: texto }));
  }

  validar(ev: Evidencia): void {
    this.enviarAvaliacao(ev, 'VALIDADA', null);
  }

  solicitarCorrecao(ev: Evidencia): void {
    const motivo = this.motivoDe(ev.evdCod).trim();
    if (!motivo) {
      this.erro.set('Informe o motivo da correção solicitada.');
      return;
    }
    this.enviarAvaliacao(ev, 'CORRECAO_SOLICITADA', motivo);
  }

  private enviarAvaliacao(ev: Evidencia, status: EStatusEvidencia, motivo: string | null): void {
    this.avaliandoCod.set(ev.evdCod);
    this.erro.set(null);
    this.evidencias.avaliar(ev.evdCod, status, motivo).subscribe({
      next: () => {
        this.motivos.update(o => ({ ...o, [ev.evdCod]: '' }));
        this.avaliandoCod.set(null);
        this.evidencias.recarregar();
        this.carregarEvidencias();
      },
      error: e => {
        this.avaliandoCod.set(null);
        this.erro.set(e?.error?.mensagem ?? 'Não foi possível avaliar a evidência.');
      },
    });
  }

  abrirArquivo(arqCod: number): void {
    this.abrindoArquivo.set(true);
    this.arquivoService.buscar(arqCod).subscribe({
      next: arq => {
        this.abrindoArquivo.set(false);
        if (arq.url) window.open(arq.url, '_blank');
      },
      error: () => {
        this.abrindoArquivo.set(false);
        this.erro.set('Não foi possível abrir o arquivo.');
      },
    });
  }
}
