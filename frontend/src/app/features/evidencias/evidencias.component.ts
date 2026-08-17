import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { EvidenciaService } from '../../core/services/evidencia/evidencia.service';
import {
  EStatusEvidencia,
  Evidencia,
  STATUS_EVIDENCIA_LABEL,
} from '../../models/evidencia/evidencia.model';
import { EvidenciaDetalheDialog } from './evidencia-detalhe.dialog';
import { EvidenciaFormDialog } from './evidencia-form.dialog';

/** Card de contagem no topo: total geral ou por status (com o filtro do status). */
interface Card {
  rotulo: string;
  quantidade: number;
  filtro: EStatusEvidencia | null;
}

/**
 * Tela "Registros de evidência": lista a versão corrente de cada evidência (título, quem registrou,
 * arquivo, status). Cards com o total e por status filtram a listagem; "Registrar evidência" abre o
 * cadastro; ABRIR mostra o histórico; CORRIGIR (só quando correção solicitada) gera a próxima versão.
 */
@Component({
  selector: 'app-evidencias',
  imports: [
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatDialogModule,
  ],
  templateUrl: './evidencias.component.html',
  styleUrl: './evidencias.component.css',
})
export class EvidenciasComponent {
  private readonly service = inject(EvidenciaService);
  private readonly dialog = inject(MatDialog);

  readonly colunas = ['titulo', 'responsavel', 'arquivo', 'status', 'acoes'];

  rotuloStatus(status: EStatusEvidencia): string {
    return STATUS_EVIDENCIA_LABEL[status];
  }

  /** Status selecionado no filtro (null = todas). */
  readonly filtro = signal<EStatusEvidencia | null>(null);

  readonly evidenciasRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listar(),
  });

  private readonly todas = computed<Evidencia[]>(() => this.evidenciasRes.value() ?? []);

  /** Listagem já aplicada o filtro de status. */
  readonly evidencias = computed<Evidencia[]>(() => {
    const status = this.filtro();
    const todas = this.todas();
    return status ? todas.filter(e => e.status === status) : todas;
  });

  /** Cards de contagem (total + um por status), calculados da própria listagem. */
  readonly cards = computed<Card[]>(() => {
    const todas = this.todas();
    const porStatus = (s: EStatusEvidencia) => todas.filter(e => e.status === s).length;
    return [
      { rotulo: 'Total', quantidade: todas.length, filtro: null },
      { rotulo: STATUS_EVIDENCIA_LABEL.EM_VALIDACAO, quantidade: porStatus('EM_VALIDACAO'), filtro: 'EM_VALIDACAO' },
      { rotulo: STATUS_EVIDENCIA_LABEL.VALIDADA, quantidade: porStatus('VALIDADA'), filtro: 'VALIDADA' },
      {
        rotulo: STATUS_EVIDENCIA_LABEL.CORRECAO_SOLICITADA,
        quantidade: porStatus('CORRECAO_SOLICITADA'),
        filtro: 'CORRECAO_SOLICITADA',
      },
    ];
  });

  filtrar(card: Card): void {
    this.filtro.set(this.filtro() === card.filtro ? null : card.filtro);
  }

  registrar(): void {
    this.dialog.open(EvidenciaFormDialog, { width: '560px', data: { modo: 'registrar' } });
  }

  corrigir(evidencia: Evidencia): void {
    this.dialog.open(EvidenciaFormDialog, {
      width: '560px',
      data: { modo: 'corrigir', evidencia },
    });
  }

  abrir(evidencia: Evidencia): void {
    this.dialog.open(EvidenciaDetalheDialog, {
      width: '620px',
      data: { evdCod: evidencia.evdCod },
    });
  }
}
