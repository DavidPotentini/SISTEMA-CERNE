import { Component, computed, inject, input, model } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { of } from 'rxjs';
import { EmpreendimentoService } from '../../../core/services/empreendimento/empreendimento.service';

/** Opção de processo do filtro (identificada pelo nome, que é único na metodologia viva). */
export interface OpcaoProcesso {
  nome: string;
}

/** Opção de prática do filtro; `processoNome` liga a prática ao seu processo (cascata). */
export interface OpcaoPratica {
  nome: string;
  processoNome: string;
}

/** Opção de status/situação — o valor e o rótulo são definidos por cada tela. */
export interface OpcaoStatus {
  value: string;
  label: string;
}

/**
 * Estado dos filtros padrão. `processo`/`pratica` são nomes (todas as telas expõem processoNome/
 * praticaNome); `respPesCod` é a pessoa; `status` é o valor do status/situação da tela. `null` = "todos".
 */
export interface FiltrosState {
  processo: string | null;
  pratica: string | null;
  respPesCod: number | null;
  status: string | null;
}

export const FILTROS_VAZIO: FiltrosState = {
  processo: null,
  pratica: null,
  respPesCod: null,
  status: null,
};

/** Verdadeiro se a linha (que expõe estes campos) casa com os filtros ativos. */
export function casaFiltros(
  filtros: FiltrosState,
  item: { processoNome?: string | null; praticaNome?: string | null; respPesCod?: number | null; status?: string | null },
): boolean {
  if (filtros.processo != null && item.processoNome !== filtros.processo) return false;
  if (filtros.pratica != null && item.praticaNome !== filtros.pratica) return false;
  if (filtros.respPesCod != null && item.respPesCod !== filtros.respPesCod) return false;
  if (filtros.status != null && item.status !== filtros.status) return false;
  return true;
}

/**
 * Barra de filtros padrão do sistema. Sempre mostra Processo → Prática (cascata); Responsável e Status
 * são opcionais por tela. O estado é um `FiltrosState` de mão dupla (`[(filtros)]`) que a tela consome
 * num `computed` para filtrar a própria lista/árvore (filtragem client-side). As opções de Responsável
 * vêm da equipe da incubadora (endpoint já existente); as de Processo/Prática/Status a tela passa.
 */
@Component({
  selector: 'app-filtros-bar',
  imports: [FormsModule, MatFormFieldModule, MatSelectModule, MatButtonModule, MatIconModule],
  templateUrl: './filtros-bar.component.html',
  styleUrl: './filtros-bar.component.css',
})
export class FiltrosBarComponent {
  private readonly empreendimentoService = inject(EmpreendimentoService);

  readonly filtros = model.required<FiltrosState>();

  readonly processos = input<OpcaoProcesso[]>([]);
  readonly praticas = input<OpcaoPratica[]>([]);
  readonly comResponsavel = input(false);
  readonly comStatus = input(false);
  readonly statusOpcoes = input<OpcaoStatus[]>([]);
  readonly statusLabel = input('Status');

  /** Equipe da incubadora — só busca quando o filtro de responsável está habilitado. */
  readonly responsaveisRes = rxResource({
    params: () => ({ on: this.comResponsavel() }),
    stream: () => (this.comResponsavel() ? this.empreendimentoService.listarResponsaveis() : of([])),
  });

  /** Práticas do processo selecionado (a prática só fica habilitada com um processo escolhido). */
  readonly praticasDoProcesso = computed<OpcaoPratica[]>(() => {
    const processo = this.filtros().processo;
    if (processo == null) {
      return [];
    }
    return this.praticas().filter(p => p.processoNome === processo);
  });

  readonly algumAtivo = computed<boolean>(() => {
    const f = this.filtros();
    return f.processo != null || f.pratica != null || f.respPesCod != null || f.status != null;
  });

  mudarProcesso(nome: string | null): void {
    this.filtros.update(f => ({ ...f, processo: nome, pratica: null }));
  }

  mudarPratica(nome: string | null): void {
    this.filtros.update(f => ({ ...f, pratica: nome }));
  }

  mudarResponsavel(respPesCod: number | null): void {
    this.filtros.update(f => ({ ...f, respPesCod }));
  }

  mudarStatus(status: string | null): void {
    this.filtros.update(f => ({ ...f, status }));
  }

  limpar(): void {
    this.filtros.set({ ...FILTROS_VAZIO });
  }
}
