import { Component, computed, inject, input, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MonitoramentoService } from '../../core/services/monitoramento/monitoramento.service';
import {
  Aplicacao,
  EEixoCerne,
  EIXO_LABEL,
  EIXOS,
  ERecomendacaoMonitor,
  EStatusMonitoramento,
  ESituacaoRodada,
  ETipoRodada,
  RECOMENDACAO_LABEL,
  Rodada,
  SITUACAO_RODADA_LABEL,
  STATUS_MONITORAMENTO_LABEL,
  TIPO_RODADA_LABEL,
} from '../../models/monitoramento/monitoramento.model';
import { RevisarAplicacaoDialog } from './revisar-aplicacao.dialog';
import { reterRecurso } from '../../shared/util/reter-recurso';

/**
 * Uma rodada na aba "Aplicações e pontuação": seção recolhível com cabeçalho (tipo/situação,
 * contagem e "Concluir rodada") e uma tabela densa — 1 linha por empreendimento, colunas dos 5 eixos
 * CERNE + recomendação + status. Aplica o filtro (nome/status) vindo da aba; expande sozinha quando o
 * filtro está ativo e há resultados.
 */
@Component({
  selector: 'app-rodada-aplicacoes',
  imports: [
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatDialogModule,
    MatTooltipModule,
  ],
  templateUrl: './rodada-aplicacoes.component.html',
  styleUrl: './rodada-aplicacoes.component.css',
})
export class RodadaAplicacoesComponent {
  private readonly service = inject(MonitoramentoService);
  private readonly dialog = inject(MatDialog);

  readonly rodada = input.required<Rodada>();
  /** Filtro por nome do empreendimento (vindo da aba). */
  readonly filtroNome = input('');
  /** Filtro por status: TODOS | CONCLUIDO | EM_ANDAMENTO | NAO_REVISADO. */
  readonly filtroStatus = input('TODOS');

  readonly eixos = EIXOS;
  readonly concluindo = signal(false);

  /** Seção aberta manualmente (o filtro pode forçar a abertura). */
  private readonly abertaManual = signal(false);

  readonly aplicacoesRes = reterRecurso(rxResource({
    params: () => ({ v: this.service.versao(), rod: this.rodada().rodCod }),
    stream: () => this.service.aplicacoes(this.rodada().rodCod),
  }));

  private readonly filtroAtivo = computed<boolean>(
    () => this.filtroNome().trim() !== '' || this.filtroStatus() !== 'TODOS',
  );

  /** Linhas após o filtro (nome/status). */
  readonly linhas = computed<Aplicacao[]>(() => {
    const nome = this.filtroNome().trim().toLowerCase();
    const status = this.filtroStatus();
    const base = this.aplicacoesRes.value() ?? [];

    const filtradas: Aplicacao[] = [];
    for (const a of base) {
      if (nome !== '' && !(a.empNome ?? '').toLowerCase().includes(nome)) {
        continue;
      }
      if (status !== 'TODOS' && this.chaveStatus(a) !== status) {
        continue;
      }
      filtradas.push(a);
    }
    return filtradas;
  });

  readonly expandida = computed<boolean>(
    () => this.abertaManual() || (this.filtroAtivo() && this.linhas().length > 0),
  );

  alternarSecao(): void {
    this.abertaManual.update(v => !v);
  }

  tipoLabel(t: ETipoRodada): string {
    return TIPO_RODADA_LABEL[t];
  }
  situacaoLabel(s: ESituacaoRodada): string {
    return SITUACAO_RODADA_LABEL[s];
  }
  statusLabel(s: EStatusMonitoramento): string {
    return STATUS_MONITORAMENTO_LABEL[s];
  }
  recomendacaoLabel(r: ERecomendacaoMonitor): string {
    return RECOMENDACAO_LABEL[r];
  }
  eixoLabel(e: EEixoCerne): string {
    return EIXO_LABEL[e];
  }

  /** Nota do eixo na aplicação, ou {@code null} se não pontuado. */
  notaDoEixo(aplicacao: Aplicacao, eixo: EEixoCerne): number | null {
    const p = aplicacao.pontuacoes.find(x => x.dimensao === eixo);
    return p != null ? p.pontuacao : null;
  }

  /** Chave de status para o filtro (não revisado quando não há status). */
  chaveStatus(a: Aplicacao): string {
    return a.status ?? 'NAO_REVISADO';
  }

  get concluida(): boolean {
    return this.rodada().situacao === 'CONCLUIDA';
  }

  revisar(aplicacao: Aplicacao): void {
    this.dialog.open(RevisarAplicacaoDialog, {
      width: '90vw',
      maxWidth: '1200px',
      data: { rodCod: this.rodada().rodCod, aplicacao },
    });
  }

  concluir(): void {
    this.concluindo.set(true);
    this.service.concluir(this.rodada().rodCod).subscribe({
      next: () => {
        this.concluindo.set(false);
        this.service.recarregar();
      },
      error: () => this.concluindo.set(false),
    });
  }
}
