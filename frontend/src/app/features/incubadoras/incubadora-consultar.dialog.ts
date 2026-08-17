import { Component, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { IncubadoraService } from '../../core/services/incubadora/incubadora.service';
import {
  ENivelIncubadora,
  EStatusIncubadora,
  IncubadoraDetalhe,
  NIVEL_LABEL,
  STATUS_LABEL,
} from '../../models/incubadora/incubadora.model';

/** Modal "Consultar": exibe todas as informações da incubadora (somente leitura). */
@Component({
  selector: 'app-incubadora-consultar',
  imports: [MatDialogModule, MatButtonModule],
  templateUrl: './incubadora-consultar.dialog.html',
  styleUrl: './incubadora-consultar.dialog.css',
})
export class IncubadoraConsultarDialog {
  private readonly service = inject(IncubadoraService);
  readonly id = inject<number>(MAT_DIALOG_DATA);
  readonly carregado = signal(false);
  dados: IncubadoraDetalhe | null = null;

  constructor() {
    this.service.buscar(this.id).subscribe(d => {
      this.dados = d;
      this.carregado.set(true);
    });
  }

  label(s: EStatusIncubadora): string {
    return STATUS_LABEL[s];
  }

  nivelLabel(n: ENivelIncubadora): string {
    return NIVEL_LABEL[n];
  }
}
