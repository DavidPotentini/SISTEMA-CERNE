import { Component, computed, effect, inject, input, model, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { of } from 'rxjs';
import { EquipeService } from '../../../core/services/equipe/equipe.service';

/** Identificada pelo nome, único na metodologia viva. */
export interface OpcaoProcesso {
  nome: string;
}

export interface OpcaoPratica {
  nome: string;
  processoNome: string;
}

export interface OpcaoStatus {
  value: string;
  label: string;
}

export interface OpcaoEmpreendimento {
  empCod: number;
  nome: string;
}

export const EMP_INSTITUCIONAL = -1;

export interface FiltrosState {
  processo: string | null;
  pratica: string | null;
  respPesCod: number | null;
  status: string | null;
  agrupamento: string | null;
  atividade: string | null;
  empCod: number | null;
  evidencia: string | null;
}

export const FILTROS_VAZIO: FiltrosState = {
  processo: null,
  pratica: null,
  respPesCod: null,
  status: null,
  agrupamento: null,
  atividade: null,
  empCod: null,
  evidencia: null,
};

function contem(alvo: string | null | undefined, busca: string): boolean {
  return (alvo ?? '').toLowerCase().includes(busca.toLowerCase());
}

export function casaFiltros(
  filtros: FiltrosState,
  item: {
    processoNome?: string | null;
    praticaNome?: string | null;
    respPesCod?: number | null;
    status?: string | null;
    agrupamentoNome?: string | null;
    nome?: string | null;
    empCod?: number | null;
  },
): boolean {
  if (filtros.processo != null && item.processoNome !== filtros.processo) return false;
  if (filtros.pratica != null && item.praticaNome !== filtros.pratica) return false;
  if (filtros.respPesCod != null && item.respPesCod !== filtros.respPesCod) return false;
  if (filtros.status != null && item.status !== filtros.status) return false;
  if (filtros.agrupamento != null && !contem(item.agrupamentoNome, filtros.agrupamento)) return false;
  if (filtros.atividade != null && !contem(item.nome, filtros.atividade)) return false;
  if (filtros.empCod != null) {
    const alvo = filtros.empCod === EMP_INSTITUCIONAL ? null : filtros.empCod;
    if ((item.empCod ?? null) !== alvo) return false;
  }
  return true;
}

@Component({
  selector: 'app-filtros-bar',
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './filtros-bar.component.html',
  styleUrl: './filtros-bar.component.css',
})
export class FiltrosBarComponent {
  private readonly equipeService = inject(EquipeService);

  readonly EMP_INSTITUCIONAL = EMP_INSTITUCIONAL;

  readonly filtros = model.required<FiltrosState>();

  readonly comProcessoPratica = input(true);
  readonly processos = input<OpcaoProcesso[]>([]);
  readonly praticas = input<OpcaoPratica[]>([]);
  readonly comResponsavel = input(false);
  readonly comStatus = input(false);
  readonly comAgrupamento = input(false);
  readonly comAtividade = input(false);
  readonly comEmpreendimento = input(false);
  readonly comEmpreendimentoInstitucional = input(true);
  readonly empreendimentos = input<OpcaoEmpreendimento[]>([]);
  readonly statusOpcoes = input<OpcaoStatus[]>([]);
  readonly statusLabel = input('Status');
  readonly comEvidencia = input(false);
  readonly evidenciaOpcoes = input<OpcaoStatus[]>([]);
  readonly evidenciaLabel = input('Situação da evidência');

  readonly rascunho = signal<FiltrosState>({ ...FILTROS_VAZIO });

  constructor() {
    effect(() => {
      this.rascunho.set({ ...this.filtros() });
    });
  }

  readonly responsaveisRes = rxResource({
    params: () => ({ on: this.comResponsavel() }),
    stream: () => (this.comResponsavel() ? this.equipeService.listarResponsaveis() : of([])),
  });

  readonly praticasDoProcesso = computed<OpcaoPratica[]>(() => {
    const processo = this.rascunho().processo;
    if (processo == null) {
      return [];
    }
    return this.praticas().filter(p => p.processoNome === processo);
  });

  readonly algumAtivo = computed<boolean>(() => {
    const f = this.filtros();
    return (
      f.processo != null ||
      f.pratica != null ||
      f.respPesCod != null ||
      f.status != null ||
      f.agrupamento != null ||
      f.atividade != null ||
      f.empCod != null ||
      f.evidencia != null
    );
  });

  mudarProcesso(nome: string | null): void {
    this.rascunho.update(f => ({ ...f, processo: nome, pratica: null }));
  }

  mudarPratica(nome: string | null): void {
    this.rascunho.update(f => ({ ...f, pratica: nome }));
  }

  mudarResponsavel(respPesCod: number | null): void {
    this.rascunho.update(f => ({ ...f, respPesCod }));
  }

  mudarStatus(status: string | null): void {
    this.rascunho.update(f => ({ ...f, status }));
  }

  mudarAgrupamento(texto: string): void {
    const agrupamento = texto.trim() || null;
    this.rascunho.update(f => ({ ...f, agrupamento }));
  }

  mudarAtividade(texto: string): void {
    const atividade = texto.trim() || null;
    this.rascunho.update(f => ({ ...f, atividade }));
  }

  mudarEmpreendimento(empCod: number | null): void {
    this.rascunho.update(f => ({ ...f, empCod }));
  }

  mudarEvidencia(evidencia: string | null): void {
    this.rascunho.update(f => ({ ...f, evidencia }));
  }

  pesquisar(): void {
    this.filtros.set({ ...this.rascunho() });
  }

  limpar(): void {
    this.rascunho.set({ ...FILTROS_VAZIO });
    this.filtros.set({ ...FILTROS_VAZIO });
  }
}
