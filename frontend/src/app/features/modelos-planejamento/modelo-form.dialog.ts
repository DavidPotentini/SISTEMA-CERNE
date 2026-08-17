import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ModelosService } from '../../core/services/modelos/modelos.service';
import { EPeriodicidade, PERIODICIDADE_LABEL } from '../../models/metodologia/metodologia.model';
import { Modelo } from '../../models/modelos/modelo.model';

interface ModeloFormData {
  /** Presente no modo edição. */
  modelo?: Modelo;
}

/**
 * Modal do cabeçalho do modelo: cria (base na última metodologia VIGENTE, resolvida no backend) ou
 * edita nome/periodicidade/descrição. Retorna o modelo salvo ao fechar.
 */
@Component({
  selector: 'app-modelo-form',
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './modelo-form.dialog.html',
  styleUrl: './modelo-form.dialog.css',
})
export class ModeloFormDialog {
  private readonly service = inject(ModelosService);
  private readonly ref = inject(MatDialogRef<ModeloFormDialog>);
  private readonly data = inject<ModeloFormData>(MAT_DIALOG_DATA);

  readonly edicao = this.data.modelo != null;
  readonly periodicidades = Object.entries(PERIODICIDADE_LABEL) as [EPeriodicidade, string][];

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly nome = signal(this.data.modelo?.nome ?? '');
  readonly periodicidade = signal<EPeriodicidade>(this.data.modelo?.periodicidade ?? 'ANUAL');
  readonly descricao = signal(this.data.modelo?.descricao ?? '');

  salvar(): void {
    if (!this.nome().trim()) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<Modelo> = {
      nome: this.nome().trim(),
      periodicidade: this.periodicidade(),
      descricao: this.descricao().trim() || null,
    };
    const req = this.data.modelo
      ? this.service.editarModelo(this.data.modelo.modCod, dto)
      : this.service.criarModelo(dto);
    req.subscribe({
      next: modelo => {
        this.service.recarregar();
        this.ref.close(modelo);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao salvar o modelo.');
      },
    });
  }
}
