import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { PlanejamentoService } from '../../core/services/planejamento/planejamento.service';
import { CicloReadonlyBannerComponent } from '../ciclos/ciclo-readonly-banner.component';
import {
  AtividadePlanejada,
  NIVEL_CERNE_LABEL,
  Planejamento,
  PlanGrupo,
  PlanPratica,
  PlanProcesso,
  STATUS_PLANEJAMENTO_LABEL,
} from '../../models/planejamento/planejamento.model';
import {
  FILTROS_VAZIO,
  FiltrosState,
  FiltrosBarComponent,
  OpcaoPratica,
  OpcaoProcesso,
} from '../../shared/ui/filtros-bar/filtros-bar.component';
import { AtividadePlanejadaFormDialog } from './atividade-planejada-form.dialog';
import { ConsultarPublicacaoDialog } from './consultar-publicacao.dialog';
import { reterRecurso } from '../../shared/util/reter-recurso';

/**
 * Tela "Planejamento institucional" do ciclo ativo. No máx. um planejamento vigente por ciclo, que é
 * materializado pelo "Gerar do ciclo" (Metodologia). "Consultar publicação" mostra status/período/
 * responsável/progresso. A estrutura de processos/práticas é a do ciclo (só leitura); as atividades
 * podem ser ajustadas e complementares incluídas/removidas enquanto o plano está publicado.
 */
@Component({
  selector: 'app-planejamento',
  imports: [
    DatePipe,
    MatCardModule,
    MatExpansionModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressBarModule,
    MatDialogModule,
    DragDropModule,
    FiltrosBarComponent,
    CicloReadonlyBannerComponent,
  ],
  templateUrl: './planejamento.component.html',
  styleUrls: ['./planejamento.component.css', '../shared/arvore-processos.css'],
})
export class PlanejamentoComponent {
  private readonly service = inject(PlanejamentoService);
  private readonly dialog = inject(MatDialog);

  readonly statusLabel = STATUS_PLANEJAMENTO_LABEL;
  /** Rótulo do nível CERNE do processo (por ora só CERNE 1). */
  readonly nivelLabel = NIVEL_CERNE_LABEL;

  readonly atualRes = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.atual(),
  }));
  readonly estruturaRes = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.estrutura(),
  }));

  readonly plano = computed(() => this.atualRes.value()?.planejamento ?? null);
  /** Publicado = ciclo vigente, atividades editáveis. */
  readonly editavel = computed(() => this.plano()?.status === 'PUBLICADO');

  /** Painéis abertos por padrão; o botão recolhe/expande a árvore toda de uma vez. */
  readonly tudoExpandido = signal(true);

  alternarTudo(): void {
    this.tudoExpandido.update(v => !v);
  }

  /** Subgrupos recolhidos (esconde as atividades); chave = `prtcCod:agrcCod` (ou `:sem` no sintético). */
  private readonly gruposRecolhidos = signal<Set<string>>(new Set());

  private chaveGrupo(prtcCod: number, g: PlanGrupo): string {
    return `${prtcCod}:${g.agrcCod ?? 'sem'}`;
  }

  grupoRecolhido(prtcCod: number, g: PlanGrupo): boolean {
    return this.gruposRecolhidos().has(this.chaveGrupo(prtcCod, g));
  }

  alternarRecolherGrupo(prtcCod: number, g: PlanGrupo): void {
    const chave = this.chaveGrupo(prtcCod, g);
    const set = new Set(this.gruposRecolhidos());
    if (set.has(chave)) {
      set.delete(chave);
    } else {
      set.add(chave);
    }
    this.gruposRecolhidos.set(set);
  }

  // ---- filtros padrão (sem status) ----
  readonly filtros = signal<FiltrosState>({ ...FILTROS_VAZIO });

  readonly processoOpcoes = computed<OpcaoProcesso[]>(() =>
    (this.estruturaRes.value() ?? []).map(p => ({ nome: p.nome })),
  );

  readonly praticaOpcoes = computed<OpcaoPratica[]>(() => {
    const out: OpcaoPratica[] = [];
    for (const proc of this.estruturaRes.value() ?? []) {
      for (const pr of proc.praticas) {
        out.push({ nome: pr.nome, processoNome: proc.nome });
      }
    }
    return out;
  });

  /**
   * Árvore com os filtros aplicados. Processo/prática recortam a estrutura (mantendo práticas vazias,
   * para ainda permitir incluir atividade); o filtro de responsável recorta atividades e esconde
   * práticas/processos que ficam sem atividade. Sem filtro, devolve a estrutura original.
   */
  readonly estruturaFiltrada = computed<PlanProcesso[]>(() => {
    const f = this.filtros();
    const procs = this.estruturaRes.value() ?? [];
    const algum = f.processo != null || f.pratica != null || f.respPesCod != null;
    if (!algum) {
      return procs;
    }
    const filtrarAtividade = f.respPesCod != null;
    const out: PlanProcesso[] = [];
    for (const proc of procs) {
      if (f.processo != null && proc.nome !== f.processo) {
        continue;
      }
      const praticas: PlanPratica[] = [];
      for (const pr of proc.praticas) {
        if (f.pratica != null && pr.nome !== f.pratica) {
          continue;
        }
        const grupos: PlanGrupo[] = [];
        for (const g of pr.grupos) {
          const atividades = filtrarAtividade
            ? g.atividades.filter(a => a.respPesCod === f.respPesCod)
            : g.atividades;
          if (filtrarAtividade && atividades.length === 0) {
            continue;
          }
          grupos.push({ ...g, atividades });
        }
        if (filtrarAtividade && grupos.length === 0) {
          continue;
        }
        praticas.push({ ...pr, grupos });
      }
      if (praticas.length === 0) {
        continue;
      }
      out.push({ ...proc, praticas });
    }
    return out;
  });

  consultar(): void {
    const plano = this.plano();
    if (!plano) return;
    this.dialog.open(ConsultarPublicacaoDialog, { width: '90vw', maxWidth: '1200px', data: plano });
  }

  /** Total de atividades da prática (soma dos grupos). */
  totalPratica(pr: PlanPratica): number {
    return pr.grupos.reduce((n, g) => n + g.atividades.length, 0);
  }

  adicionarAtividade(pr: PlanPratica): void {
    this.dialog.open(AtividadePlanejadaFormDialog, {
      width: '90vw', maxWidth: '1200px',
      data: { prtcCod: pr.prtcCod, pratica: pr.nome },
    });
  }

  ajustarAtividade(pr: PlanPratica, atv: AtividadePlanejada): void {
    this.dialog.open(AtividadePlanejadaFormDialog, {
      width: '90vw', maxWidth: '1200px',
      data: { prtcCod: pr.prtcCod, pratica: pr.nome, atividade: atv },
    });
  }

  removerAtividade(atv: AtividadePlanejada): void {
    const ok = window.confirm(`Excluir a atividade "${atv.nome}" do planejamento?`);
    if (!ok) return;
    this.service.removerAtividade(atv.atpCod).subscribe(() => this.service.recarregar());
  }

  /**
   * Arrastar-e-soltar de atividades DENTRO de um grupo; persiste a nova sequência de `atpCod` da prática
   * inteira (grupos na ordem + "sem agrupamento"), com o grupo movido reordenado internamente.
   */
  reordenarAtividade(pr: PlanPratica, agrcCod: number | null, event: CdkDragDrop<AtividadePlanejada[]>): void {
    if (event.previousIndex === event.currentIndex) return;
    const atpCods: number[] = [];
    for (const g of pr.grupos) {
      if (g.agrcCod === agrcCod) {
        const lista = [...g.atividades];
        moveItemInArray(lista, event.previousIndex, event.currentIndex);
        atpCods.push(...lista.map(a => a.atpCod));
      } else {
        atpCods.push(...g.atividades.map(a => a.atpCod));
      }
    }
    this.service.reordenarAtividades(pr.prtcCod, atpCods).subscribe(() => this.service.recarregar());
  }

  /**
   * Arrastar-e-soltar de agrupamentos de uma prática; persiste a sequência de `agrcCod`. O grupo
   * sintético "Sem agrupamento" (sempre o último e não arrastável) fica fora: o destino é limitado aos
   * grupos reais.
   */
  reordenarAgrupamento(pr: PlanPratica, event: CdkDragDrop<PlanGrupo[]>): void {
    const reais = pr.grupos.filter(g => g.agrcCod != null);
    const to = Math.min(event.currentIndex, reais.length - 1);
    if (event.previousIndex === to) return;
    const copia = [...reais];
    moveItemInArray(copia, event.previousIndex, to);
    this.service
      .reordenarAgrupamentos(pr.prtcCod, copia.map(g => g.agrcCod!))
      .subscribe(() => this.service.recarregar());
  }

  progresso(p: Planejamento): number {
    return p.progresso;
  }
}
