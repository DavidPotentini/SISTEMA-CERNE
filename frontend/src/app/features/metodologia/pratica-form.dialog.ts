import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import { Pratica } from '../../models/metodologia/metodologia.model';

interface PraticaFormData {
  prcCod: number;
  processo: string;
  /** Presente no modo edição. */
  pratica?: Pratica;
}

/** Modal de prática: adiciona ou edita nome/descrição, no processo (accordion) de onde foi aberta. */
@Component({
  selector: 'app-pratica-form',
  imports: [FormsModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './pratica-form.dialog.html',
  styleUrl: './pratica-form.dialog.css',
})
export class PraticaFormDialog {
  private readonly service = inject(MetodologiaService);
  private readonly ref = inject(MatDialogRef<PraticaFormDialog>);
  private readonly data = inject<PraticaFormData>(MAT_DIALOG_DATA);

  readonly processo = this.data.processo;
  readonly edicao = this.data.pratica != null;

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly nome = signal(this.data.pratica?.nome ?? '');
  readonly descricao = signal(this.data.pratica?.descricao ?? '');

  salvar(): void {
    if (!this.nome().trim()) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<Pratica> = {
      nome: this.nome().trim(),
      descricao: this.descricao().trim() || null,
    };
    const req = this.data.pratica
      ? this.service.editarPratica(this.data.prcCod, this.data.pratica.prtCod, dto)
      : this.service.adicionarPratica(this.data.prcCod, dto);
    req.subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao salvar a prática.');
      },
    });
  }
}
