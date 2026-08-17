import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import {
  Planejamento,
  STATUS_PLANEJAMENTO_LABEL,
} from '../../models/planejamento/planejamento.model';

/**
 * Modal "Consultar publicação": mostra, só leitura, o status do planejamento, o período (início/fim),
 * o responsável e o progresso (% de atividades concluídas).
 */
@Component({
  selector: 'app-consultar-publicacao',
  imports: [DatePipe, MatDialogModule, MatButtonModule, MatProgressBarModule],
  templateUrl: './consultar-publicacao.dialog.html',
  styleUrl: './consultar-publicacao.dialog.css',
})
export class ConsultarPublicacaoDialog {
  readonly plano = inject<Planejamento>(MAT_DIALOG_DATA);
  readonly statusLabel = STATUS_PLANEJAMENTO_LABEL;
}
