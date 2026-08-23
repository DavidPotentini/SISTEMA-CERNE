import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { format, parseISO } from 'date-fns';
import { PainelOperacionalService } from '../../core/services/painel/painel-operacional.service';
import {
  ETipoPendencia,
  Pendencia,
  TIPO_PENDENCIA_ICONE,
  TIPO_PENDENCIA_LABEL,
  TIPO_PENDENCIA_ROTA,
  TIPOS_PENDENCIA,
} from '../../models/painel/painel-operacional.model';
import {
  FILTROS_VAZIO,
  FiltrosState,
  FiltrosBarComponent,
  OpcaoPratica,
  OpcaoProcesso,
  OpcaoStatus,
  casaFiltros,
} from '../../shared/ui/filtros-bar/filtros-bar.component';

/** Uma seção do painel: o tipo, seu rótulo/ícone e as pendências daquele tipo. */
interface Secao {
  tipo: ETipoPendencia;
  rotulo: string;
  icone: string;
  itens: Pendencia[];
}

/**
 * Tela "Painel Operacional": reúne todas as pendências do ciclo ativo. Cards-resumo no topo (contagem
 * por tipo) e, abaixo, uma seção expansível por tipo com seus itens. Cada item é clicável e navega
 * até a tela de origem (acompanhamento, evidências ou apuração). Contadores e agrupamento derivados
 * no front a partir da lista única do endpoint.
 */
@Component({
  selector: 'app-painel-operacional',
  imports: [
    MatButtonModule,
    MatExpansionModule,
    MatIconModule,
    MatProgressBarModule,
    FiltrosBarComponent,
  ],
  templateUrl: './painel-operacional.component.html',
  styleUrl: './painel-operacional.component.css',
})
export class PainelOperacionalComponent {
  private readonly service = inject(PainelOperacionalService);
  private readonly router = inject(Router);

  readonly pendenciasRes = rxResource({
    stream: () => this.service.pendencias(),
  });

  private readonly todas = computed<Pendencia[]>(() => this.pendenciasRes.value() ?? []);

  // ---- filtros padrão (status = tipo de pendência) ----
  readonly filtros = signal<FiltrosState>({ ...FILTROS_VAZIO });

  readonly statusOpcoes: OpcaoStatus[] = TIPOS_PENDENCIA.map(tipo => ({
    value: tipo,
    label: TIPO_PENDENCIA_LABEL[tipo],
  }));

  readonly processoOpcoes = computed<OpcaoProcesso[]>(() => {
    const nomes = new Set<string>();
    for (const p of this.todas()) {
      if (p.processoNome) nomes.add(p.processoNome);
    }
    return [...nomes].map(nome => ({ nome }));
  });

  readonly praticaOpcoes = computed<OpcaoPratica[]>(() => {
    const mapa = new Map<string, OpcaoPratica>();
    for (const p of this.todas()) {
      if (p.processoNome && p.praticaNome && !mapa.has(p.praticaNome)) {
        mapa.set(p.praticaNome, { nome: p.praticaNome, processoNome: p.processoNome });
      }
    }
    return [...mapa.values()];
  });

  private readonly itensFiltrados = computed<Pendencia[]>(() => {
    const f = this.filtros();
    return this.todas().filter(p =>
      casaFiltros(f, {
        processoNome: p.processoNome,
        praticaNome: p.praticaNome,
        respPesCod: p.respPesCod,
        status: p.tipo,
      }),
    );
  });

  /** Seções na ordem canônica (mais urgente primeiro), só as com itens (após os filtros). */
  readonly secoes = computed<Secao[]>(() => {
    const itens = this.itensFiltrados();
    return TIPOS_PENDENCIA.map(tipo => ({
      tipo,
      rotulo: TIPO_PENDENCIA_LABEL[tipo],
      icone: TIPO_PENDENCIA_ICONE[tipo],
      itens: itens.filter(p => p.tipo === tipo),
    })).filter(s => s.itens.length > 0);
  });

  /** Cards-resumo: contagem por tipo, na ordem canônica (sempre os 4). */
  readonly resumo = computed(() => {
    const itens = this.pendenciasRes.value() ?? [];
    return TIPOS_PENDENCIA.map(tipo => ({
      tipo,
      rotulo: TIPO_PENDENCIA_LABEL[tipo],
      icone: TIPO_PENDENCIA_ICONE[tipo],
      quantidade: itens.filter(p => p.tipo === tipo).length,
    }));
  });

  readonly total = computed(() => (this.pendenciasRes.value() ?? []).length);

  abrir(pendencia: Pendencia): void {
    // Deep-link: a tela de destino lê o parâmetro e abre o diálogo específico. Evidência abre em
    // "Corrigir"; atividade e meta abrem em "Registrar".
    const queryParams =
      pendencia.tipo === 'EVIDENCIA_CORRECAO'
        ? { corrigir: pendencia.referenciaId }
        : { registrar: pendencia.referenciaId };
    this.router.navigate([TIPO_PENDENCIA_ROTA[pendencia.tipo]], { queryParams });
  }

  atualizar(): void {
    this.pendenciasRes.reload();
  }

  formatarData(iso: string | null): string {
    return iso == null ? '' : format(parseISO(iso), 'dd/MM/yyyy');
  }
}
