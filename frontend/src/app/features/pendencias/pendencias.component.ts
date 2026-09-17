import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { format, parseISO } from 'date-fns';
import { PendenciasService } from '../../core/services/painel/pendencias.service';
import { CicloReadonlyBannerComponent } from '../ciclos/ciclo-readonly-banner.component';
import {
  ETipoPendencia,
  Pendencia,
  TIPO_PENDENCIA_ICONE,
  TIPO_PENDENCIA_LABEL,
  TIPO_PENDENCIA_ROTA,
  TIPOS_PENDENCIA,
} from '../../models/painel/pendencias.model';
import {
  FILTROS_VAZIO,
  FiltrosState,
  FiltrosBarComponent,
  OpcaoPratica,
  OpcaoProcesso,
  OpcaoStatus,
  casaFiltros,
} from '../../shared/ui/filtros-bar/filtros-bar.component';

interface Secao {
  tipo: ETipoPendencia;
  rotulo: string;
  icone: string;
  itens: Pendencia[];
}

@Component({
  selector: 'app-pendencias',
  imports: [
    MatButtonModule,
    MatExpansionModule,
    MatIconModule,
    MatProgressBarModule,
    FiltrosBarComponent,
    CicloReadonlyBannerComponent,
  ],
  templateUrl: './pendencias.component.html',
  styleUrl: './pendencias.component.css',
})
export class PendenciasComponent {
  private readonly service = inject(PendenciasService);
  private readonly router = inject(Router);

  readonly pendenciasRes = rxResource({
    stream: () => this.service.pendencias(),
  });

  private readonly todas = computed<Pendencia[]>(() => this.pendenciasRes.value() ?? []);

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

  readonly secoes = computed<Secao[]>(() => {
    const itens = this.itensFiltrados();
    return TIPOS_PENDENCIA.map(tipo => ({
      tipo,
      rotulo: TIPO_PENDENCIA_LABEL[tipo],
      icone: TIPO_PENDENCIA_ICONE[tipo],
      itens: itens.filter(p => p.tipo === tipo),
    })).filter(s => s.itens.length > 0);
  });

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

  readonly tudoExpandido = signal(true);

  alternarTudo(): void {
    this.tudoExpandido.update(v => !v);
  }

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
