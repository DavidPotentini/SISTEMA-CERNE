import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import { Agrupamento } from '../../models/metodologia/metodologia.model';

interface AgrupamentoFormData {
  prtCod: number;
  pratica: string;
  agrupamento?: Agrupamento;
}

@Component({
  selector: 'app-agrupamento-form',
  imports: [FormsModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './agrupamento-form.dialog.html',
  styleUrl: './pratica-form.dialog.css',
})
export class AgrupamentoFormDialog {
  private readonly service = inject(MetodologiaService);
  private readonly ref = inject(MatDialogRef<AgrupamentoFormDialog>);
  private readonly data = inject<AgrupamentoFormData>(MAT_DIALOG_DATA);

  readonly pratica = this.data.pratica;
  readonly edicao = this.data.agrupamento != null;

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly nome = signal(this.data.agrupamento?.nome ?? '');
  readonly descricao = signal(this.data.agrupamento?.descricao ?? '');

  salvar(): void {
    if (!this.nome().trim()) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<Agrupamento> = {
      nome: this.nome().trim(),
      descricao: this.descricao().trim() || null,
    };
    const req = this.data.agrupamento
      ? this.service.editarAgrupamento(this.data.agrupamento.agrCod, dto)
      : this.service.adicionarAgrupamento(this.data.prtCod, dto);
    req.subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao salvar o agrupamento.');
      },
    });
  }
}
