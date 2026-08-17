import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { ModelosService } from '../../core/services/modelos/modelos.service';
import { Modelo } from '../../models/modelos/modelo.model';
import { PlanejamentoService } from '../../core/services/planejamento/planejamento.service';
import { Planejamento } from '../../models/planejamento/planejamento.model';

interface GerarModeloData {
  /** Nome do ciclo ativo (contexto no cabeçalho). */
  cicloNome: string | null;
  /** Já existe um planejamento vigente? (gera aviso de substituição). */
  substitui: boolean;
}

/**
 * Modal "Gerar de modelo": escolhe um modelo publicado e gera o planejamento do ciclo ativo. Se já
 * houver um vigente, avisa que ele será substituído (o anterior é inativado). Retorna o plano gerado.
 */
@Component({
  selector: 'app-gerar-modelo',
  imports: [FormsModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatSelectModule],
  templateUrl: './gerar-modelo.dialog.html',
  styleUrl: './gerar-modelo.dialog.css',
})
export class GerarModeloDialog {
  private readonly modelosService = inject(ModelosService);
  private readonly service = inject(PlanejamentoService);
  private readonly ref = inject(MatDialogRef<GerarModeloDialog>);
  readonly data = inject<GerarModeloData>(MAT_DIALOG_DATA);

  readonly carregando = signal(true);
  readonly gerando = signal(false);
  readonly erro = signal<string | null>(null);
  readonly modelos = signal<Modelo[]>([]);
  readonly modCod = signal<number | null>(null);

  constructor() {
    this.modelosService.listarModelos().subscribe({
      next: lista => {
        this.modelos.set(lista.filter(m => m.status === 'PUBLICADO'));
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar os modelos.');
        this.carregando.set(false);
      },
    });
  }

  gerar(): void {
    const modCod = this.modCod();
    if (modCod == null) return;
    this.gerando.set(true);
    this.erro.set(null);
    this.service.gerar(modCod).subscribe({
      next: (plano: Planejamento) => {
        this.service.recarregar();
        this.ref.close(plano);
      },
      error: e => {
        this.gerando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao gerar o planejamento.');
      },
    });
  }
}
