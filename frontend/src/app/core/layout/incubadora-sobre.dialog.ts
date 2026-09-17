import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MinhaIncubadoraService } from '../services/minha-incubadora/minha-incubadora.service';
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

@Component({
  selector: 'app-incubadora-sobre',
  imports: [FormsModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './incubadora-sobre.dialog.html',
  styleUrl: './incubadora-sobre.dialog.css',
})
export class IncubadoraSobreDialog {
  private readonly data = inject<IncubadoraSobreData>(MAT_DIALOG_DATA);
  private readonly service = inject(MinhaIncubadoraService);
  private readonly ref = inject(MatDialogRef<IncubadoraSobreDialog>);

  dados: IncubadoraDetalhe = { ...this.data.incubadora };

  readonly editando = signal(false);
  readonly salvando = signal(false);

  get inc(): IncubadoraDetalhe {
    return this.dados;
  }

  statusLabel(s: EStatusIncubadora): string {
    return STATUS_LABEL[s];
  }

  nivelLabel(n: ENivelIncubadora): string {
    return NIVEL_LABEL[n];
  }

  editar(): void {
    this.editando.set(true);
  }

  cancelarEdicao(): void {
    this.dados = { ...this.data.incubadora };
    this.editando.set(false);
  }

  salvar(): void {
    if (!this.dados.nome.trim()) return;
    this.salvando.set(true);
    this.service.atualizar(this.dados).subscribe({
      next: () => this.ref.close(true),
      error: () => this.salvando.set(false),
    });
  }
}
