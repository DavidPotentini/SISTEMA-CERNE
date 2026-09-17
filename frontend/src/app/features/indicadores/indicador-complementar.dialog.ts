import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { EquipeService } from '../../core/services/equipe/equipe.service';
import { IndicadorService } from '../../core/services/indicador/indicador.service';
import { IndicadorCiclo, PraticaOpcao } from '../../models/indicador/indicador.model';
import {
  EPeriodicidade,
  PERIODICIDADE_LABEL,
} from '../../models/metodologia/metodologia.model';

interface Opcao {
  cod: number;
  nome: string | null;
}

@Component({
  selector: 'app-indicador-complementar',
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './indicador-complementar.dialog.html',
  styleUrl: './indicador-complementar.dialog.css',
})
export class IndicadorComplementarDialog {
  private readonly service = inject(IndicadorService);
  private readonly equipeService = inject(EquipeService);
  private readonly ref = inject(MatDialogRef<IndicadorComplementarDialog>);

  readonly periodicidades = Object.entries(PERIODICIDADE_LABEL) as [EPeriodicidade, string][];

  readonly responsaveisRes = rxResource({
    stream: () => this.equipeService.listarResponsaveis(),
  });

  readonly opcoes = signal<PraticaOpcao[]>([]);
  readonly prcCod = signal<number | null>(null);

  readonly processos = computed<Opcao[]>(() => {
    const mapa = new Map<number, Opcao>();
    for (const o of this.opcoes()) {
      if (!mapa.has(o.prccCod)) mapa.set(o.prccCod, { cod: o.prccCod, nome: o.processoNome });
    }
    return [...mapa.values()];
  });

  readonly praticas = computed<PraticaOpcao[]>(() =>
    this.opcoes().filter(o => o.prccCod === this.prcCod()),
  );

  readonly nome = signal('');
  readonly prtCod = signal<number | null>(null);
  readonly unidade = signal('');
  readonly periodicidade = signal<EPeriodicidade>('TRIMESTRAL');
  readonly respPesCod = signal<number | null>(null);

  processoAlterado(): void {
    this.prtCod.set(null);
  }

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  constructor() {
    this.service.vinculos().subscribe({
      next: lista => this.opcoes.set(lista),
    });
  }

  salvar(): void {
    if (!this.nome().trim()) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<IndicadorCiclo> = {
      nome: this.nome().trim(),
      prtcCod: this.prtCod(),
      unidade: this.unidade().trim() || null,
      periodicidade: this.periodicidade(),
      respPesCod: this.respPesCod(),
    };
    this.service.definirComplementar(dto).subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao definir o indicador complementar.');
      },
    });
  }
}
