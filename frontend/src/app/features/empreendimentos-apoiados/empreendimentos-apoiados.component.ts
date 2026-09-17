import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { EmpreendimentoService } from '../../core/services/empreendimento/empreendimento.service';
import { PlanejamentoService } from '../../core/services/planejamento/planejamento.service';
import {
  EEstagioIncubacao,
  ENivelMaturidade,
  ESituacaoContrato,
  EStatusEmpreendimento,
  Empreendimento,
  ESTAGIO_LABEL,
  NIVEL_MATURIDADE_LABEL,
  SITUACAO_CONTRATO_LABEL,
  STATUS_EMP_LABEL,
} from '../../models/empreendimento/empreendimento.model';
import {
  EMP_INSTITUCIONAL,
  FILTROS_VAZIO,
  FiltrosBarComponent,
  FiltrosState,
  OpcaoEmpreendimento,
} from '../../shared/ui/filtros-bar/filtros-bar.component';
import { CicloReadonlyBannerComponent } from '../ciclos/ciclo-readonly-banner.component';

interface Consolidacao {
  rotulo: string;
  contagem: number;
  pct: number;
}

interface PraticaProgresso {
  nome: string;
  total: number;
  concluidas: number;
  progresso: number;
}

interface ProcessoProgresso extends PraticaProgresso {
  praticas: PraticaProgresso[];
}

interface EmpSituacao extends PraticaProgresso {
  empCod: number;
  processos: ProcessoProgresso[];
}

@Component({
  selector: 'app-empreendimentos-apoiados',
  imports: [
    MatCardModule,
    MatTableModule,
    MatIconModule,
    MatProgressBarModule,
    MatTooltipModule,
    FiltrosBarComponent,
    CicloReadonlyBannerComponent,
  ],
  templateUrl: './empreendimentos-apoiados.component.html',
  styleUrl: './empreendimentos-apoiados.component.css',
})
export class EmpreendimentosApoiadosComponent {
  private readonly empService = inject(EmpreendimentoService);
  private readonly planService = inject(PlanejamentoService);

  readonly colunas = ['expandir', 'nome', 'cnpj', 'estagio', 'maturidade', 'contrato', 'status', 'progresso'];

  readonly filtros = signal<FiltrosState>({ ...FILTROS_VAZIO });
  readonly expandido = signal<Set<number>>(new Set());


  readonly empresasRes = rxResource({
    params: () => ({ v: this.empService.versao() }),
    stream: () => this.empService.listarDoCiclo(),
  });
  readonly estruturaRes = rxResource({
    params: () => ({ v: this.planService.versao() }),
    stream: () => this.planService.estrutura(),
  });

  readonly empresas = computed<Empreendimento[]>(() => this.empresasRes.value() ?? []);

  readonly empOpcoes = computed<OpcaoEmpreendimento[]>(() =>
    this.empresas().map(e => ({ empCod: e.empCod, nome: e.nome })),
  );

  readonly empresasFiltradas = computed<Empreendimento[]>(() => {
    const emp = this.filtros().empCod;
    if (emp == null) return this.empresas();
    if (emp === EMP_INSTITUCIONAL) return [];
    return this.empresas().filter(e => e.empCod === emp);
  });

  readonly porEstagio = computed(() =>
    this.consolidar(this.empresas().map(e => e.estagio), ESTAGIO_LABEL),
  );
  readonly porStatus = computed(() =>
    this.consolidar(this.empresas().map(e => e.status), STATUS_EMP_LABEL),
  );
  readonly porContrato = computed(() =>
    this.consolidar(this.empresas().map(e => e.situacaoContrato), SITUACAO_CONTRATO_LABEL),
  );
  readonly porMaturidade = computed(() =>
    this.consolidar(this.empresas().map(e => e.nivelMaturidade), NIVEL_MATURIDADE_LABEL),
  );

  readonly situacao = computed<EmpSituacao[]>(() => {
    const estrutura = this.estruturaRes.value() ?? [];
    return this.empresas().map(emp => {
      const processos: ProcessoProgresso[] = [];
      let empTotal = 0;
      let empConcl = 0;
      for (const proc of estrutura) {
        const praticas: PraticaProgresso[] = [];
        let procTotal = 0;
        let procConcl = 0;
        for (const prat of proc.praticas) {
          let total = 0;
          let concl = 0;
          for (const g of prat.grupos) {
            for (const a of g.atividades) {
              if (a.empCod !== emp.empCod) continue;
              total++;
              if (a.status === 'CONCLUIDA') concl++;
            }
          }
          if (total > 0) {
            praticas.push({ nome: prat.nome, total, concluidas: concl, progresso: this.pct(concl, total) });
            procTotal += total;
            procConcl += concl;
          }
        }
        if (procTotal > 0) {
          processos.push({
            nome: proc.nome,
            total: procTotal,
            concluidas: procConcl,
            progresso: this.pct(procConcl, procTotal),
            praticas,
          });
          empTotal += procTotal;
          empConcl += procConcl;
        }
      }
      return {
        empCod: emp.empCod,
        nome: emp.nome,
        total: empTotal,
        concluidas: empConcl,
        progresso: this.pct(empConcl, empTotal),
        processos,
      };
    });
  });

  readonly situacaoPorEmp = computed<Map<number, EmpSituacao>>(() => {
    const mapa = new Map<number, EmpSituacao>();
    for (const s of this.situacao()) mapa.set(s.empCod, s);
    return mapa;
  });

  estaExpandido(empCod: number): boolean {
    return this.expandido().has(empCod);
  }

  alternar(empCod: number): void {
    this.expandido.update(atual => {
      const nova = new Set(atual);
      if (nova.has(empCod)) nova.delete(empCod);
      else nova.add(empCod);
      return nova;
    });
  }

  situacaoDe(empCod: number): EmpSituacao | null {
    return this.situacaoPorEmp().get(empCod) ?? null;
  }

  estagioLabel(v: EEstagioIncubacao | null): string {
    return v ? ESTAGIO_LABEL[v] : '—';
  }

  maturidadeLabel(v: ENivelMaturidade | null): string {
    return v ? NIVEL_MATURIDADE_LABEL[v] : '—';
  }

  contratoLabel(v: ESituacaoContrato | null): string {
    return v ? SITUACAO_CONTRATO_LABEL[v] : '—';
  }

  statusLabel(v: EStatusEmpreendimento): string {
    return STATUS_EMP_LABEL[v];
  }

  fmtData(d: string | null): string {
    return d ? d.split('-').reverse().join('/') : '—';
  }

  periodo(e: Empreendimento): string {
    if (e.entrada && e.saida) return `${this.fmtData(e.entrada)} – ${this.fmtData(e.saida)}`;
    if (e.entrada) return `${this.fmtData(e.entrada)} – atual`;
    if (e.saida) return `até ${this.fmtData(e.saida)}`;
    return '—';
  }

  siteUrl(s: string): string {
    return /^https?:\/\//i.test(s) ? s : `https://${s}`;
  }

  instagramUrl(h: string): string {
    return /^https?:\/\//i.test(h) ? h : `https://instagram.com/${h.replace(/^@/, '')}`;
  }

  private pct(parte: number, total: number): number {
    return total > 0 ? Math.round((parte / total) * 100) : 0;
  }

  private consolidar<T extends string>(valores: (T | null)[], labels: Record<T, string>): Consolidacao[] {
    const total = valores.length;
    const contagem = new Map<string, number>();
    for (const v of valores) {
      const chave = v ?? '';
      contagem.set(chave, (contagem.get(chave) ?? 0) + 1);
    }
    const out: Consolidacao[] = [];
    for (const chave of Object.keys(labels) as T[]) {
      const n = contagem.get(chave) ?? 0;
      if (n > 0) out.push({ rotulo: labels[chave], contagem: n, pct: this.pct(n, total) });
    }
    const nulos = contagem.get('') ?? 0;
    if (nulos > 0) out.push({ rotulo: 'Não informado', contagem: nulos, pct: this.pct(nulos, total) });
    return out;
  }
}
