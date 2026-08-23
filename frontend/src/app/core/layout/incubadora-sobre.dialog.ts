import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import {
  ENivelIncubadora,
  EStatusIncubadora,
  IncubadoraDetalhe,
  NIVEL_LABEL,
  STATUS_LABEL,
} from '../../models/incubadora/incubadora.model';

interface IncubadoraSobreData {
  incubadora: IncubadoraDetalhe;
}

/**
 * Diálogo "Sobre a incubadora": ficha institucional em só-leitura (CNPJ, mantenedora, responsável,
 * contatos, cidade, nível). Aberto pelo botão do rodapé do menu — informação pouco consultada, tirada
 * da tela Minha Incubadora para não competir com os cards operacionais. Recebe a incubadora já
 * carregada pelo layout (sem novo fetch).
 */
@Component({
  selector: 'app-incubadora-sobre',
  imports: [MatDialogModule, MatButtonModule],
  templateUrl: './incubadora-sobre.dialog.html',
  styleUrl: './incubadora-sobre.dialog.css',
})
export class IncubadoraSobreDialog {
  private readonly data = inject<IncubadoraSobreData>(MAT_DIALOG_DATA);

  readonly inc = this.data.incubadora;

  statusLabel(s: EStatusIncubadora): string {
    return STATUS_LABEL[s];
  }

  nivelLabel(n: ENivelIncubadora): string {
    return NIVEL_LABEL[n];
  }
}
