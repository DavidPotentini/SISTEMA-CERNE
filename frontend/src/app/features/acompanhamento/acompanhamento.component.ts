import { DatePipe } from '@angular/common';
import { Component, computed, effect, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { differenceInCalendarDays, parseISO } from 'date-fns';
import { PlanejamentoService } from '../../core/services/planejamento/planejamento.service';
import { EmpreendimentoService } from '../../core/services/empreendimento/empreendimento.service';
import { EvidenciaService } from '../../core/services/evidencia/evidencia.service';
import { CicloReadonlyBannerComponent } from '../ciclos/ciclo-readonly-banner.component';
import { ProximoPassoComponent } from '../../shared/ui/proximo-passo/proximo-passo.component';
import {
  AtividadePlanejada,
  EStatusAtividade,
  PlanGrupo,
  PlanPratica,
  PlanProcesso,
  STATUS_ATIVIDADE_LABEL,
  STATUS_PLANEJAMENTO_LABEL,
} from '../../models/planejamento/planejamento.model';
import {
  EMP_INSTITUCIONAL,
  FILTROS_VAZIO,
  FiltrosState,
  FiltrosBarComponent,
  OpcaoPratica,
  OpcaoProcesso,
  OpcaoStatus,
} from '../../shared/ui/filtros-bar/filtros-bar.component';
import { AtividadeRegistroDialog } from './atividade-registro.dialog';
import { reterRecurso } from '../../shared/util/reter-recurso';

const STATUS_FILTRAVEIS: EStatusAtividade[] = ['PLANEJADA', 'EM_ANDAMENTO', 'CONCLUIDA', 'ATRASADA'];

interface ContagemEvid {
  validadas: number;
  pendentes: number;
  correcao: number;
  total: number;
}
const CONTAGEM_VAZIA: ContagemEvid = { validadas: 0, pendentes: 0, correcao: 0, total: 0 };

const STATUS_ICONE: Record<EStatusAtividade, string> = {
  PLANEJADA: 'schedule',
  EM_ANDAMENTO: 'autorenew',
  CONCLUIDA: 'check_circle',
  ATRASADA: 'error',
};

@Component({
  selector: 'app-acompanhamento',
  imports: [
    DatePipe,
    MatCardModule,
    MatExpansionModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatDialogModule,
    FiltrosBarComponent,
    CicloReadonlyBannerComponent,
    ProximoPassoComponent,
  ],
  templateUrl: './acompanhamento.component.html',
  styleUrls: ['./acompanhamento.component.css', '../shared/arvore-processos.css'],
})
export class AcompanhamentoComponent {
  private readonly service = inject(PlanejamentoService);
  private readonly empreendimentoService = inject(EmpreendimentoService);
  private readonly evidenciaService = inject(EvidenciaService);
  private readonly dialog = inject(MatDialog);

  readonly statusIcone = STATUS_ICONE;
  private readonly route = inject(ActivatedRoute);

  private registroAberto = false;

  readonly statusPlanoLabel = STATUS_PLANEJAMENTO_LABEL;
  readonly statusLabel = STATUS_ATIVIDADE_LABEL;

  readonly atualRes = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.atual(),
  }));
  readonly estruturaRes = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.estrutura(),
  }));

  readonly plano = computed(() => this.atualRes.value()?.planejamento ?? null);

  readonly empreendimentosRes = rxResource({
    params: () => ({ v: this.empreendimentoService.versao() }),
    stream: () => this.empreendimentoService.listarDoCiclo(),
  });
  readonly empreendimentos = computed(() => this.empreendimentosRes.value() ?? []);

  readonly evidenciasRes = reterRecurso(rxResource({
    params: () => ({ p: this.service.versao(), e: this.evidenciaService.versao() }),
    stream: () => this.evidenciaService.listar(),
  }));

  readonly contagensPorAtp = computed<Map<number, ContagemEvid>>(() => {
    const mapa = new Map<number, ContagemEvid>();
    for (const ev of this.evidenciasRes.value() ?? []) {
      const c = mapa.get(ev.atpCod) ?? { ...CONTAGEM_VAZIA };
      if (ev.status === 'VALIDADA') c.validadas++;
      else if (ev.status === 'PENDENTE_VALIDACAO') c.pendentes++;
      else if (ev.status === 'CORRECAO_SOLICITADA') c.correcao++;
      c.total++;
      mapa.set(ev.atpCod, c);
    }
    return mapa;
  });

  contagemEvid(atpCod: number): ContagemEvid {
    return this.contagensPorAtp().get(atpCod) ?? CONTAGEM_VAZIA;
  }

  readonly tudoExpandido = signal(true);

  alternarTudo(): void {
    this.tudoExpandido.update(v => !v);
  }

  private readonly gruposRecolhidos = signal<Set<string>>(new Set());

  private chaveGrupo(prtcCod: number, g: PlanGrupo): string {
    return `${prtcCod}:${g.agrcCod ?? 'sem'}`;
  }

  grupoRecolhido(prtcCod: number, g: PlanGrupo): boolean {
    if (this.filtroAtivoExpandeGp()) {
      return false;
    }
    return !this.gruposRecolhidos().has(this.chaveGrupo(prtcCod, g));
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

  readonly filtros = signal<FiltrosState>({ ...FILTROS_VAZIO });

  readonly statusOpcoes: OpcaoStatus[] = STATUS_FILTRAVEIS.map(s => ({
    value: s,
    label: STATUS_ATIVIDADE_LABEL[s],
  }));

  readonly evidenciaOpcoes: OpcaoStatus[] = [
    { value: 'VALIDADA', label: 'Com evidência validada' },
    { value: 'PENDENTE', label: 'Com evidência pendente' },
    { value: 'CORRECAO', label: 'Com evidência em correção' },
    { value: 'SEM', label: 'Sem evidências' },
  ];

  private casaEvidencia(atpCod: number, filtro: string | null): boolean {
    if (filtro == null) {
      return true;
    }
    const c = this.contagemEvid(atpCod);
    if (filtro === 'VALIDADA') return c.validadas > 0;
    if (filtro === 'PENDENTE') return c.pendentes > 0;
    if (filtro === 'CORRECAO') return c.correcao > 0;
    return c.total === 0;
  }

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

  readonly algumFiltroAtivo = computed<boolean>(() => {
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

  readonly filtroAtivoExpandeGp = computed<boolean>(() => {
    const f = this.filtros();
    return (
      // f.processo != null ||
      // f.pratica != null ||
      f.respPesCod != null ||
      f.status != null ||
      // f.agrupamento != null ||
      f.atividade != null ||
      f.empCod != null ||
      f.evidencia != null
    );
  });

  readonly estruturaFiltrada = computed<PlanProcesso[]>(() => {
    const f = this.filtros();
    const procs = this.estruturaRes.value() ?? [];
    if (!this.algumFiltroAtivo()) {
      return procs;
    }
    const alvoEmp = f.empCod === EMP_INSTITUCIONAL ? null : f.empCod;
    const buscaAtv = f.atividade?.toLowerCase() ?? null;
    const buscaGrp = f.agrupamento?.toLowerCase() ?? null;
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
          if (buscaGrp != null && !g.nome.toLowerCase().includes(buscaGrp)) {
            continue;
          }
          const atividades = g.atividades.filter(
            a =>
              (f.respPesCod == null || a.respPesCod === f.respPesCod) &&
              (f.status == null || a.status === f.status) &&
              (buscaAtv == null || a.nome.toLowerCase().includes(buscaAtv)) &&
              (f.empCod == null || (a.empCod ?? null) === alvoEmp) &&
              this.casaEvidencia(a.atpCod, f.evidencia),
          );
          if (atividades.length > 0) {
            grupos.push({ ...g, atividades });
          }
        }
        if (grupos.length > 0) {
          praticas.push({ ...pr, grupos });
        }
      }
      if (praticas.length > 0) {
        out.push({ ...proc, praticas });
      }
    }
    return out;
  });

  constructor() {
    // Vindo de Pendências (?registrar=atpCod): abre o modal uma única vez quando a estrutura
    // carrega — recarregar a estrutura não deve reabrir o modal.
    effect(() => {
      const processos = this.estruturaRes.value();
      if (!processos || this.registroAberto) {
        return;
      }
      const atpCod = Number(this.route.snapshot.queryParamMap.get('registrar'));
      if (!atpCod) {
        return;
      }
      const atividade = processos
        .flatMap(p => p.praticas)
        .flatMap(pr => pr.grupos)
        .flatMap(g => g.atividades)
        .find(a => a.atpCod === atpCod);
      if (atividade) {
        this.registroAberto = true;
        this.registrar(atividade);
      }
    });
  }

  atividadesDe(proc: PlanProcesso): AtividadePlanejada[] {
    return proc.praticas.flatMap(pr => this.atividadesDePratica(pr));
  }

  atividadesDePratica(pr: PlanPratica): AtividadePlanejada[] {
    return pr.grupos.flatMap(g => g.atividades);
  }

  total(proc: PlanProcesso): number {
    return this.atividadesDe(proc).length;
  }

  concluidas(proc: PlanProcesso): number {
    return this.atividadesDe(proc).filter(a => a.status === 'CONCLUIDA').length;
  }

  progresso(proc: PlanProcesso): number {
    const t = this.total(proc);
    return t === 0 ? 0 : Math.round((this.concluidas(proc) / t) * 100);
  }

  concluidasPratica(pr: PlanPratica): number {
    return this.atividadesDePratica(pr).filter(a => a.status === 'CONCLUIDA').length;
  }

  totalPratica(pr: PlanPratica): number {
    return this.atividadesDePratica(pr).length;
  }

  concluidasGrupo(g: PlanGrupo): number {
    return g.atividades.filter(a => a.status === 'CONCLUIDA').length;
  }

  resumoAtividades(atvs: AtividadePlanejada[]): { status: EStatusAtividade; count: number }[] {
    const cont = new Map<EStatusAtividade, number>();
    for (const a of atvs) {
      cont.set(a.status, (cont.get(a.status) ?? 0) + 1);
    }
    return STATUS_FILTRAVEIS.map(s => ({ status: s, count: cont.get(s) ?? 0 })).filter(x => x.count > 0);
  }

  resumoEvidencias(atvs: AtividadePlanejada[]): ContagemEvid {
    const acc: ContagemEvid = { ...CONTAGEM_VAZIA };
    for (const a of atvs) {
      const c = this.contagemEvid(a.atpCod);
      acc.validadas += c.validadas;
      acc.pendentes += c.pendentes;
      acc.correcao += c.correcao;
      acc.total += c.total;
    }
    return acc;
  }

  prazoRelativo(atv: AtividadePlanejada): string {
    if (!atv.prazo || atv.status === 'CONCLUIDA') {
      return '';
    }
    const dias = differenceInCalendarDays(parseISO(atv.prazo), new Date());
    if (dias < 0) {
      const n = -dias;
      return `atrasada há ${n} ${n === 1 ? 'dia' : 'dias'}`;
    }
    if (dias === 0) {
      return 'vence hoje';
    }
    return `vence em ${dias} ${dias === 1 ? 'dia' : 'dias'}`;
  }

  prazoVencido(atv: AtividadePlanejada): boolean {
    if (!atv.prazo || atv.status === 'CONCLUIDA') {
      return false;
    }
    return differenceInCalendarDays(parseISO(atv.prazo), new Date()) < 0;
  }

  registrar(atv: AtividadePlanejada): void {
    this.dialog.open(AtividadeRegistroDialog, {
      width: '90vw', maxWidth: '1200px',
      data: { atividade: atv },
    });
  }
}
