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
import { addMonths, format, parseISO } from 'date-fns';
import { CicloService } from '../../core/services/ciclo/ciclo.service';
import { IndicadorService } from '../../core/services/indicador/indicador.service';
import { Ciclo } from '../../models/ciclo/ciclo.model';
import { IndicadorCiclo, Meta } from '../../models/indicador/indicador.model';
import {
  EPeriodicidade,
  PERIODICIDADE_LABEL,
} from '../../models/metodologia/metodologia.model';

interface MetaIndicadorData {
  indicador: IndicadorCiclo;
}

/** Duração de um período em meses, por periodicidade. POR_CICLO/NAO_SE_APLICA não têm passo fixo. */
const MESES_POR_PERIODICIDADE: Partial<Record<EPeriodicidade, number>> = {
  MENSAL: 1,
  BIMESTRAL: 2,
  TRIMESTRAL: 3,
  SEMESTRAL: 6,
  ANUAL: 12,
};

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
  private readonly cicloService = inject(CicloService);
  private readonly data = inject<MetaIndicadorData>(MAT_DIALOG_DATA);

  readonly indicador = this.data.indicador;

  readonly carregando = signal(true);
  readonly metas = signal<Meta[]>([]);
  readonly erro = signal<string | null>(null);

  /** Ciclo ativo — usado para inferir o período quando a periodicidade é POR_CICLO. */
  private readonly cicloAtivo = signal<Ciclo | null>(null);

  // formulário de período (novo ou edição)
  readonly editandoCod = signal<number | null>(null);
  readonly valor = signal<number | null>(null);
  readonly inicio = signal<string | null>(null);
  readonly fim = signal<string | null>(null);
  readonly salvando = signal(false);

  /** Último fim sugerido automaticamente; quando o usuário altera para outro valor, pedimos confirmação. */
  private readonly fimSugerido = signal<string | null>(null);

  constructor() {
    this.carregar();
    this.cicloService.listar().subscribe(lista => {
      this.cicloAtivo.set(lista.find(c => c.status === 'ATIVO') ?? null);
      this.prefillPorCiclo();
    });
  }

  /** Em POR_CICLO, ao abrir um período novo, pré-preenche a janela com o período do ciclo ativo. */
  private prefillPorCiclo(): void {
    if (this.indicador.periodicidade !== 'POR_CICLO' || this.editando) return;
    const ciclo = this.cicloAtivo();
    if (!ciclo) return;
    if (this.inicio() == null && ciclo.inicio) this.inicio.set(ciclo.inicio);
    if (this.fim() == null && ciclo.fim) {
      this.fim.set(ciclo.fim);
      this.fimSugerido.set(ciclo.fim);
    }
  }

  periodicidadeLabel(p: EPeriodicidade): string {
    return PERIODICIDADE_LABEL[p];
  }

  /**
   * Ao informar o início da apuração, sugere o fim somando os meses da periodicidade
   * (bimestral → +2 meses). POR_CICLO (janela do ciclo, ver {@link prefillPorCiclo}) e
   * NAO_SE_APLICA não têm passo fixo — o fim fica manual. É só sugestão: o fim continua editável.
   */
  sugerirFim(valor: string | null): void {
    this.inicio.set(valor || null);

    const meses = MESES_POR_PERIODICIDADE[this.indicador.periodicidade];
    if (!valor || meses == null) {
      this.fimSugerido.set(null);
      return;
    }
    const fim = format(addMonths(parseISO(valor), meses), 'yyyy-MM-dd');
    this.fim.set(fim);
    this.fimSugerido.set(fim);
  }

  /**
   * Handler do campo Fim: se o usuário mudar a data sugerida automaticamente, pede confirmação.
   * Ao confirmar, mantém o valor digitado; ao cancelar, restaura a sugestão.
   */
  aoMudarFim(valor: string | null): void {
    const novo = valor || null;
    const sugerido = this.fimSugerido();
    if (sugerido != null && novo != null && novo !== sugerido) {
      const ok = window.confirm(
        `O fim sugerido pela periodicidade é ${this.formatarBR(sugerido)}. ` +
          `Confirmar a alteração para ${this.formatarBR(novo)}?`,
      );
      if (!ok) {
        // restaura a sugestão (novo → sugerido é mudança real, reflete no campo)
        this.fim.set(novo);
        this.fim.set(sugerido);
        return;
      }
      this.fimSugerido.set(null); // alteração aceita: não perguntar de novo
    }
    this.fim.set(novo);
  }

  private formatarBR(iso: string): string {
    return format(parseISO(iso), 'dd/MM/yyyy');
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
    this.fimSugerido.set(null); // valores existentes: sem sugestão a confirmar
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
    this.fimSugerido.set(null);
    this.prefillPorCiclo();
  }
}
