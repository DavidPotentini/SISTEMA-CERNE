import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import { AtividadeMetodologia } from '../../models/metodologia/metodologia.model';

interface AtividadeFormData {
  /** Presente no modo edição. */
  atividade?: AtividadeMetodologia;
  /** No modo criação a partir de uma prática: pré-seleciona o vínculo. */
  prtCod?: number;
}

/**
 * Modal de atividade-padrão da metodologia: cria ou edita. O "Vínculo metodológico" é a prática,
 * escolhida num seletor agrupado por processo. "Quem"/"quando" não entram aqui (só no planejamento).
 */
@Component({
  selector: 'app-atividade-form',
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './atividade-form.dialog.html',
  styleUrl: './atividade-form.dialog.css',
})
export class AtividadeFormDialog {
  private readonly service = inject(MetodologiaService);
  private readonly ref = inject(MatDialogRef<AtividadeFormDialog>);
  private readonly data = inject<AtividadeFormData | null>(MAT_DIALOG_DATA);

  readonly edicao = this.data?.atividade != null;

  /** Processos (com práticas) da metodologia, para o seletor de vínculo. */
  readonly processos = rxResource({ stream: () => this.service.listarProcessos() });

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly prtCod = signal<number | null>(this.data?.atividade?.prtCod ?? this.data?.prtCod ?? null);
  readonly nome = signal(this.data?.atividade?.nome ?? '');
  readonly observacoes = signal(this.data?.atividade?.observacoes ?? '');

  salvar(): void {
    const prtCod = this.prtCod();
    if (!this.nome().trim() || prtCod == null) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<AtividadeMetodologia> = {
      prtCod,
      nome: this.nome().trim(),
      observacoes: this.observacoes().trim() || null,
    };
    const req = this.data?.atividade
      ? this.service.editarAtividade(this.data.atividade.ameCod, dto)
      : this.service.criarAtividade(dto);
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
