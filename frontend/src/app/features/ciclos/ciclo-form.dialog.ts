import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CicloService } from '../../core/services/ciclo/ciclo.service';
import { Ciclo } from '../../models/ciclo/ciclo.model';

/**
 * Modal "Novo ciclo": nome + período. O ciclo nasce ativo e encerra o ativo anterior (regra no
 * backend). O foco é definido depois, pelo botão "Pôr em foco" na listagem.
 */
@Component({
  selector: 'app-ciclo-form',
  imports: [FormsModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './ciclo-form.dialog.html',
  styleUrl: './ciclo-form.dialog.css',
})
export class CicloFormDialog {
  private readonly service = inject(CicloService);
  private readonly ref = inject(MatDialogRef<CicloFormDialog>);

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly nome = signal('');
  readonly inicio = signal('');
  readonly fim = signal('');

  salvar(): void {
    if (!this.nome().trim()) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<Ciclo> = {
      nome: this.nome().trim(),
      inicio: this.inicio() || null,
      fim: this.fim() || null,
      emFoco: false
    };
    this.service.criar(dto).subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao criar o ciclo.');
      },
    });
  }
}
