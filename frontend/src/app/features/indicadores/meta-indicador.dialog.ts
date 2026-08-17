import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { IndicadorService } from '../../core/services/indicador/indicador.service';
import { IndicadorCiclo, Meta } from '../../models/indicador/indicador.model';
import {
  EPeriodicidade,
  PERIODICIDADE_LABEL,
} from '../../models/metodologia/metodologia.model';

interface MetaIndicadorData {
  indicador: IndicadorCiclo;
}

/**
 * Modal de meta: mostra o indicador (só leitura) e a lista de períodos, cada um com a meta estipulada
 * e a janela de apuração. Permite cadastrar, editar e remover períodos no mesmo formulário.
 */
@Component({
  selector: 'app-meta-indicador',
  imports: [
    DatePipe,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressBarModule,
  ],
  templateUrl: './meta-indicador.dialog.html',
  styleUrl: './meta-indicador.dialog.css',
})
export class MetaIndicadorDialog {
  private readonly service = inject(IndicadorService);
  private readonly data = inject<MetaIndicadorData>(MAT_DIALOG_DATA);

  readonly indicador = this.data.indicador;

  readonly carregando = signal(true);
  readonly metas = signal<Meta[]>([]);
  readonly erro = signal<string | null>(null);

  // formulário de período (novo ou edição)
  readonly editandoCod = signal<number | null>(null);
  readonly valor = signal<number | null>(null);
  readonly inicio = signal<string | null>(null);
  readonly fim = signal<string | null>(null);
  readonly salvando = signal(false);

  constructor() {
    this.carregar();
  }

  periodicidadeLabel(p: EPeriodicidade): string {
    return PERIODICIDADE_LABEL[p];
  }

  private carregar(): void {
    this.carregando.set(true);
    this.service.listarMetas(this.indicador.indCod).subscribe({
      next: lista => {
        this.metas.set(lista);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar os períodos.');
        this.carregando.set(false);
      },
    });
  }

  get editando(): boolean {
    return this.editandoCod() != null;
  }

  get podeSalvar(): boolean {
    return (
      this.valor() != null && this.inicio() != null && this.fim() != null && !this.salvando()
    );
  }

  editar(m: Meta): void {
    this.editandoCod.set(m.metCod);
    this.valor.set(m.valor);
    this.inicio.set(m.dataInicioApuracao);
    this.fim.set(m.dataFimApuracao);
    this.erro.set(null);
  }

  cancelarEdicao(): void {
    this.limparForm();
  }

  salvar(): void {
    if (!this.podeSalvar) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<Meta> = {
      valor: this.valor(),
      dataInicioApuracao: this.inicio(),
      dataFimApuracao: this.fim(),
    };
    const cod = this.editandoCod();
    const req = cod
      ? this.service.editarMeta(this.indicador.indCod, cod, dto)
      : this.service.criarMeta(this.indicador.indCod, dto);
    req.subscribe({
      next: () => {
        this.salvando.set(false);
        this.limparForm();
        this.carregar();
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao salvar o período.');
      },
    });
  }

  remover(m: Meta): void {
    this.erro.set(null);
    this.service.removerMeta(this.indicador.indCod, m.metCod).subscribe({
      next: () => {
        if (this.editandoCod() === m.metCod) this.limparForm();
        this.carregar();
      },
      error: e => this.erro.set(e?.error?.mensagem ?? 'Falha ao remover o período.'),
    });
  }

  private limparForm(): void {
    this.editandoCod.set(null);
    this.valor.set(null);
    this.inicio.set(null);
    this.fim.set(null);
  }
}
