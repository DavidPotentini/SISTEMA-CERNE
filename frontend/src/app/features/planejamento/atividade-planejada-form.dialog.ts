import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { EmpreendimentoService } from '../../core/services/empreendimento/empreendimento.service';
import { PlanejamentoService } from '../../core/services/planejamento/planejamento.service';
import { AtividadePlanejada } from '../../models/planejamento/planejamento.model';

interface AtividadePlanejadaFormData {
  prtCod: number;
  /** Nome da prática (contexto no cabeçalho do modal). */
  pratica: string;
  /** Presente no modo ajuste (edição). */
  atividade?: AtividadePlanejada;
}

/**
 * Modal de atividade planejada: ajusta uma existente (do modelo ou complementar) ou inclui uma
 * complementar na prática de onde foi aberta. Campos: nome, observação e prazo.
 */
@Component({
  selector: 'app-atividade-planejada-form',
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './atividade-planejada-form.dialog.html',
  styleUrl: './atividade-planejada-form.dialog.css',
})
export class AtividadePlanejadaFormDialog {
  private readonly service = inject(PlanejamentoService);
  private readonly empreendimentoService = inject(EmpreendimentoService);
  private readonly ref = inject(MatDialogRef<AtividadePlanejadaFormDialog>);
  private readonly data = inject<AtividadePlanejadaFormData>(MAT_DIALOG_DATA);

  readonly pratica = this.data.pratica;
  readonly edicao = this.data.atividade != null;

  readonly responsaveisRes = rxResource({
    stream: () => this.empreendimentoService.listarResponsaveis(),
  });

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly nome = signal(this.data.atividade?.nome ?? '');
  readonly observacoes = signal(this.data.atividade?.observacoes ?? '');
  readonly respPesCod = signal<number | null>(this.data.atividade?.respPesCod ?? null);
  readonly prazo = signal(this.data.atividade?.prazo ?? '');

  salvar(): void {
    if (!this.nome().trim()) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<AtividadePlanejada> = {
      nome: this.nome().trim(),
      observacoes: this.observacoes().trim() || null,
      respPesCod: this.respPesCod(),
      prazo: this.prazo() || null,
    };
    const req = this.data.atividade
      ? this.service.ajustarAtividade(this.data.atividade.atpCod, dto)
      : this.service.adicionarComplementar(this.data.prtCod, dto);
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
