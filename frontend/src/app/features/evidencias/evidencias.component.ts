import { Component, computed, effect, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { EvidenciaService } from '../../core/services/evidencia/evidencia.service';
import { CicloReadonlyBannerComponent } from '../ciclos/ciclo-readonly-banner.component';
import {
  EStatusEvidencia,
  Evidencia,
  STATUS_EVIDENCIA_LABEL,
} from '../../models/evidencia/evidencia.model';
import {
  FILTROS_VAZIO,
  FiltrosState,
  FiltrosBarComponent,
  OpcaoPratica,
  OpcaoProcesso,
  OpcaoStatus,
  casaFiltros,
} from '../../shared/ui/filtros-bar/filtros-bar.component';
import { EvidenciaDetalheDialog } from './evidencia-detalhe.dialog';
import { EvidenciaFormDialog } from './evidencia-form.dialog';

/**
 * Tela "Registros de evidência": lista a versão corrente de cada evidência (título, atividade/contexto,
 * quem registrou, arquivo, status). A barra de filtros padrão (Processo/Prática/Responsável/Status)
 * recorta a listagem; "Registrar evidência" abre o cadastro; ABRIR mostra o histórico; CORRIGIR (só
 * quando correção solicitada) gera a próxima versão.
 */
@Component({
  selector: 'app-evidencias',
  imports: [
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatDialogModule,
    FiltrosBarComponent,
    CicloReadonlyBannerComponent,
  ],
  templateUrl: './evidencias.component.html',
  styleUrl: './evidencias.component.css',
})
export class EvidenciasComponent {
  private readonly service = inject(EvidenciaService);
  private readonly dialog = inject(MatDialog);
  private readonly route = inject(ActivatedRoute);

  /** Deep-link do Painel Operacional já tratado? (evita reabrir quando a lista recarrega). */
  private correcaoAberta = false;

  readonly colunas = ['titulo', 'atividade', 'responsavel', 'arquivo', 'status', 'acoes'];

  readonly evidenciasRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listar(),
  });

  readonly todas = computed<Evidencia[]>(() => this.evidenciasRes.value() ?? []);

  // ---- filtros padrão ----
  readonly filtros = signal<FiltrosState>({ ...FILTROS_VAZIO });

  readonly statusOpcoes: OpcaoStatus[] = (
    Object.entries(STATUS_EVIDENCIA_LABEL) as [EStatusEvidencia, string][]
  ).map(([value, label]) => ({ value, label }));

  readonly processoOpcoes = computed<OpcaoProcesso[]>(() => {
    const nomes = new Set<string>();
    for (const e of this.todas()) {
      if (e.processoNome) nomes.add(e.processoNome);
    }
    return [...nomes].map(nome => ({ nome }));
  });

  readonly praticaOpcoes = computed<OpcaoPratica[]>(() => {
    const mapa = new Map<string, OpcaoPratica>();
    for (const e of this.todas()) {
      if (e.processoNome && e.praticaNome && !mapa.has(e.praticaNome)) {
        mapa.set(e.praticaNome, { nome: e.praticaNome, processoNome: e.processoNome });
      }
    }
    return [...mapa.values()];
  });

  /** Listagem já com os filtros aplicados (responsável = quem registrou, regPesCod). */
  readonly evidencias = computed<Evidencia[]>(() => {
    const f = this.filtros();
    return this.todas().filter(e =>
      casaFiltros(f, {
        processoNome: e.processoNome,
        praticaNome: e.praticaNome,
        respPesCod: e.regPesCod,
        status: e.status,
      }),
    );
  });

  constructor() {
    // Vindo do Painel Operacional (?corrigir=evdCod): abre "Corrigir" da evidência quando a lista
    // carregar. Só uma vez, e só se ainda estiver em correção solicitada.
    effect(() => {
      const todas = this.evidenciasRes.value();
      if (!todas || this.correcaoAberta) {
        return;
      }
      const evdCod = Number(this.route.snapshot.queryParamMap.get('corrigir'));
      if (!evdCod) {
        return;
      }
      const evidencia = todas.find(e => e.evdCod === evdCod);
      if (evidencia && evidencia.status === 'CORRECAO_SOLICITADA') {
        this.correcaoAberta = true;
        this.corrigir(evidencia);
      }
    });
  }

  rotuloStatus(status: EStatusEvidencia): string {
    return STATUS_EVIDENCIA_LABEL[status];
  }

  registrar(): void {
    this.dialog.open(EvidenciaFormDialog, { width: '560px', data: { modo: 'registrar' } });
  }

  corrigir(evidencia: Evidencia): void {
    this.dialog.open(EvidenciaFormDialog, {
      width: '560px',
      data: { modo: 'corrigir', evidencia },
    });
  }

  abrir(evidencia: Evidencia): void {
    this.dialog.open(EvidenciaDetalheDialog, {
      width: '620px',
      data: { evdCod: evidencia.evdCod },
    });
  }
}
