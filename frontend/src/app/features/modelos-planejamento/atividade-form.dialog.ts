import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ModelosService } from '../../core/services/modelos/modelos.service';
import { AtividadeModelo } from '../../models/modelos/modelo.model';

interface AtividadeFormData {
  modCod: number;
  prtCod: number;
  /** Nome da prática (contexto no cabeçalho do modal). */
  pratica: string;
  /** Presente no modo edição. */
  atividade?: AtividadeModelo;
}

/** Modal de atividade: adiciona ou edita nome/observação na prática de onde foi aberta. */
@Component({
  selector: 'app-atividade-form',
  imports: [FormsModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './atividade-form.dialog.html',
  styleUrl: './atividade-form.dialog.css',
})
export class AtividadeFormDialog {
  private readonly service = inject(ModelosService);
  private readonly ref = inject(MatDialogRef<AtividadeFormDialog>);
  private readonly data = inject<AtividadeFormData>(MAT_DIALOG_DATA);

  readonly pratica = this.data.pratica;
  readonly edicao = this.data.atividade != null;

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly nome = signal(this.data.atividade?.nome ?? '');
  readonly observacoes = signal(this.data.atividade?.observacoes ?? '');

  salvar(): void {
    if (!this.nome().trim()) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<AtividadeModelo> = {
      nome: this.nome().trim(),
      observacoes: this.observacoes().trim() || null,
    };
    const req = this.data.atividade
      ? this.service.editarAtividade(this.data.modCod, this.data.atividade.atmCod, dto)
      : this.service.adicionarAtividade(this.data.modCod, this.data.prtCod, dto);
    req.subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao salvar a atividade.');
      },
    });
  }
}
