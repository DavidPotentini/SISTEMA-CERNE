import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import { Processo } from '../../models/metodologia/metodologia.model';

interface ProcessoFormData {
  verCod: number;
  /** Presente no modo edição. */
  processo?: Processo;
}

/** Modal de processo: cria (na versão vigente) ou edita ordem/nome/descrição. Ordem é única na versão. */
@Component({
  selector: 'app-processo-form',
  imports: [FormsModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './processo-form.dialog.html',
  styleUrl: './processo-form.dialog.css',
})
export class ProcessoFormDialog {
  private readonly service = inject(MetodologiaService);
  private readonly ref = inject(MatDialogRef<ProcessoFormDialog>);
  private readonly data = inject<ProcessoFormData>(MAT_DIALOG_DATA);

  readonly edicao = this.data.processo != null;

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly nome = signal(this.data.processo?.nome ?? '');
  readonly ordem = signal<number | null>(this.data.processo?.ordem ?? null);
  readonly descricao = signal(this.data.processo?.descricao ?? '');

  salvar(): void {
    const ordem = this.ordem();
    if (!this.nome().trim() || ordem == null) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<Processo> = {
      verCod: this.data.verCod,
      ordem,
      nome: this.nome().trim(),
      descricao: this.descricao().trim() || null,
    };
    const req = this.data.processo
      ? this.service.editarProcesso(this.data.processo.prcCod, dto)
      : this.service.criarProcesso(dto);
    req.subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao salvar o processo.');
      },
    });
  }
}
