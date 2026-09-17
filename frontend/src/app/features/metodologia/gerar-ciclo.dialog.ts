import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import { EmpreendimentoOpcao } from '../../models/metodologia/metodologia.model';

interface GerarCicloData {
  ciclo: string;
}

@Component({
  selector: 'app-gerar-ciclo',
  imports: [MatDialogModule, MatButtonModule, MatCheckboxModule, MatProgressBarModule],
  templateUrl: './gerar-ciclo.dialog.html',
  styleUrl: './gerar-ciclo.dialog.css',
})
export class GerarCicloDialog {
  private readonly service = inject(MetodologiaService);
  private readonly ref = inject(MatDialogRef<GerarCicloDialog>);
  readonly data = inject<GerarCicloData>(MAT_DIALOG_DATA);

  readonly carregando = signal(true);
  readonly erro = signal<string | null>(null);
  readonly incubadas = signal<EmpreendimentoOpcao[]>([]);
  readonly selecionadas = signal<Set<number>>(new Set());

  constructor() {
    this.service.opcoesGerar().subscribe({
      next: o => {
        this.incubadas.set(o.incubadas);
        this.selecionadas.set(new Set(o.selecionadas));
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar as incubadas.');
        this.carregando.set(false);
      },
    });
  }

  marcada(empCod: number): boolean {
    return this.selecionadas().has(empCod);
  }

  alternar(empCod: number): void {
    this.selecionadas.update(atual => {
      const nova = new Set(atual);
      if (nova.has(empCod)) {
        nova.delete(empCod);
      } else {
        nova.add(empCod);
      }
      return nova;
    });
  }

  gerar(): void {
    this.ref.close([...this.selecionadas()]);
  }
}
