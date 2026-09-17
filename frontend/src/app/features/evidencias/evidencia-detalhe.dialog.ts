import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ArquivoService } from '../../core/services/arquivo/arquivo.service';
import { EvidenciaService } from '../../core/services/evidencia/evidencia.service';
import {
  Evidencia,
  STATUS_EVIDENCIA_LABEL,
} from '../../models/evidencia/evidencia.model';

interface EvidenciaDetalheData {
  evdCod: number;
}

@Component({
  selector: 'app-evidencia-detalhe',
  imports: [DatePipe, MatDialogModule, MatButtonModule, MatIconModule, MatProgressBarModule],
  templateUrl: './evidencia-detalhe.dialog.html',
  styleUrl: './evidencia-detalhe.dialog.css',
})
export class EvidenciaDetalheDialog {
  private readonly service = inject(EvidenciaService);
  private readonly arquivoService = inject(ArquivoService);
  private readonly data = inject<EvidenciaDetalheData>(MAT_DIALOG_DATA);

  readonly statusLabel = STATUS_EVIDENCIA_LABEL;

  readonly carregando = signal(true);
  readonly versoes = signal<Evidencia[]>([]);
  readonly erro = signal<string | null>(null);
  readonly abrindoArquivo = signal(false);

  constructor() {
    this.service.historico(this.data.evdCod).subscribe({
      next: lista => {
        this.versoes.set(lista);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar o histórico da evidência.');
        this.carregando.set(false);
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
