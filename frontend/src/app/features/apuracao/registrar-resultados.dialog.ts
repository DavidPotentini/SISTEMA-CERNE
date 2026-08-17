import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ApuracaoService } from '../../core/services/apuracao/apuracao.service';
import { ApuracaoIndicador, PeriodoApuracao } from '../../models/indicador/indicador.model';
import {
  EPeriodicidade,
  PERIODICIDADE_LABEL,
} from '../../models/metodologia/metodologia.model';

interface RegistrarResultadosData {
  indicador: ApuracaoIndicador;
}

/**
 * Modal "Registrar Resultados": mostra o indicador (só leitura) e a lista de períodos, cada um com a
 * meta e — quando apurado — o resultado (valor, registrado por, data). Registrar/editar define o
 * valor do período no mesmo lugar; quem registrou e a data são carimbados pelo backend. A lista
 * observa o `versao` do service, então um `recarregar()` após salvar atualiza modal e tela de trás.
 */
@Component({
  selector: 'app-registrar-resultados',
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
  templateUrl: './registrar-resultados.dialog.html',
  styleUrl: './registrar-resultados.dialog.css',
})
export class RegistrarResultadosDialog {
  private readonly service = inject(ApuracaoService);
  private readonly data = inject<RegistrarResultadosData>(MAT_DIALOG_DATA);

  readonly indicador = this.data.indicador;

  readonly periodosRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.periodos(this.indicador.indCod),
  });

  readonly erro = signal<string | null>(null);

  // registro inline do período selecionado
  readonly registrandoCod = signal<number | null>(null);
  readonly valor = signal<number | null>(null);
  readonly salvando = signal(false);

  periodicidadeLabel(p: EPeriodicidade): string {
    return PERIODICIDADE_LABEL[p];
  }

  registrar(p: PeriodoApuracao): void {
    this.registrandoCod.set(p.metCod);
    this.valor.set(p.resultadoValor);
    this.erro.set(null);
  }

  cancelar(): void {
    this.registrandoCod.set(null);
    this.valor.set(null);
  }

  get podeSalvar(): boolean {
    return this.valor() != null && !this.salvando();
  }

  salvar(metCod: number): void {
    if (!this.podeSalvar) return;
    this.salvando.set(true);
    this.erro.set(null);
    this.service.registrar(this.indicador.indCod, metCod, this.valor()!).subscribe({
      next: () => {
        this.salvando.set(false);
        this.cancelar();
        this.service.recarregar();
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao registrar o resultado.');
      },
    });
  }
}
