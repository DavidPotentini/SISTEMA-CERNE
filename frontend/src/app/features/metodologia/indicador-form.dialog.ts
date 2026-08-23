import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import {
  EPeriodicidade,
  Indicador,
  PERIODICIDADE_LABEL,
} from '../../models/metodologia/metodologia.model';

interface IndicadorFormData {
  /** Presente no modo edição. */
  indicador?: Indicador;
}

/**
 * Modal de indicador: cria ou edita. O "Vínculo metodológico" é a prática, escolhida num seletor
 * agrupado por processo (carregado da metodologia).
 */
@Component({
  selector: 'app-indicador-form',
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './indicador-form.dialog.html',
  styleUrl: './indicador-form.dialog.css',
})
export class IndicadorFormDialog {
  private readonly service = inject(MetodologiaService);
  private readonly ref = inject(MatDialogRef<IndicadorFormDialog>);
  private readonly data = inject<IndicadorFormData | null>(MAT_DIALOG_DATA);

  readonly edicao = this.data?.indicador != null;
  readonly periodicidades = Object.entries(PERIODICIDADE_LABEL) as [EPeriodicidade, string][];

  /** Processos (com práticas) da metodologia, para o seletor de vínculo. */
  readonly processos = rxResource({ stream: () => this.service.listarProcessos() });

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly prtCod = signal<number | null>(this.data?.indicador?.prtCod ?? null);
  readonly nome = signal(this.data?.indicador?.nome ?? '');
  readonly unidade = signal(this.data?.indicador?.unidade ?? '');
  readonly periodicidade = signal<EPeriodicidade>(
    this.data?.indicador?.periodicidade ?? 'NAO_SE_APLICA',
  );

  salvar(): void {
    const prtCod = this.prtCod();
    if (!this.nome().trim() || prtCod == null) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<Indicador> = {
      prtCod,
      nome: this.nome().trim(),
      unidade: this.unidade().trim() || null,
      periodicidade: this.periodicidade(),
    };
    const req = this.data?.indicador
      ? this.service.editarIndicador(this.data.indicador.inmCod, dto)
      : this.service.criarIndicador(dto);
    req.subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao salvar o indicador.');
      },
    });
  }
}
