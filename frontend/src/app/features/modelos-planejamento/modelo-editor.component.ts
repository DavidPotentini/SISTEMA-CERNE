import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute, Router } from '@angular/router';
import { ModelosService } from '../../core/services/modelos/modelos.service';
import { PERIODICIDADE_LABEL } from '../../models/metodologia/metodologia.model';
import {
  AtividadeModelo,
  ModeloPratica,
  ModeloProcesso,
  STATUS_MODELO_LABEL,
} from '../../models/modelos/modelo.model';
import {
  FILTROS_VAZIO,
  FiltrosState,
  FiltrosBarComponent,
  OpcaoPratica,
  OpcaoProcesso,
} from '../../shared/ui/filtros-bar/filtros-bar.component';
import { AtividadeFormDialog } from './atividade-form.dialog';
import { ModeloFormDialog } from './modelo-form.dialog';

/**
 * Editor de um modelo. Cabeçalho com dados do modelo (editáveis só em RASCUNHO) e a estrutura de
 * processos/práticas herdada da metodologia (só leitura), com as atividades de cada prática. Só as
 * atividades são editáveis/desativáveis, e apenas enquanto o modelo é RASCUNHO — publicado é imutável.
 */
@Component({
  selector: 'app-modelo-editor',
  imports: [
    DatePipe,
    MatCardModule,
    MatExpansionModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatDialogModule,
    FiltrosBarComponent,
  ],
  templateUrl: './modelo-editor.component.html',
  styleUrl: './modelo-editor.component.css',
})
export class ModeloEditorComponent {
  private readonly service = inject(ModelosService);
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly modCod = Number(this.route.snapshot.paramMap.get('modCod'));

  readonly periodicidadeLabel = PERIODICIDADE_LABEL;
  readonly statusLabel = STATUS_MODELO_LABEL;
  readonly publicando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly modeloRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.obterModelo(this.modCod),
  });
  readonly estruturaRes = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.estrutura(this.modCod),
  });

  /** Rascunho = editável; publicado = imutável (esconde ações). */
  readonly editavel = computed(() => this.modeloRes.value()?.status === 'RASCUNHO');

  // ---- filtros padrão (só processo/prática) ----
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

  /** Estrutura com os filtros de processo/prática aplicados; sem filtro, devolve a original. */
  readonly estruturaFiltrada = computed<ModeloProcesso[]>(() => {
    const f = this.filtros();
    const procs = this.estruturaRes.value() ?? [];
    if (f.processo == null && f.pratica == null) {
      return procs;
    }
    const out: ModeloProcesso[] = [];
    for (const proc of procs) {
      if (f.processo != null && proc.nome !== f.processo) {
        continue;
      }
      let praticas = proc.praticas;
      if (f.pratica != null) {
        praticas = praticas.filter(pr => pr.nome === f.pratica);
        if (praticas.length === 0) {
          continue;
        }
      }
      out.push({ ...proc, praticas });
    }
    return out;
  });

  voltar(): void {
    this.router.navigate(['/incubadora/modelos']);
  }

  editarDados(): void {
    const modelo = this.modeloRes.value();
    if (!modelo) return;
    this.dialog.open(ModeloFormDialog, { width: '560px', data: { modelo } });
  }

  publicar(): void {
    if (!this.editavel()) return;
    this.publicando.set(true);
    this.erro.set(null);
    this.service.publicarModelo(this.modCod).subscribe({
      next: () => {
        this.publicando.set(false);
        this.service.recarregar();
      },
      error: e => {
        this.publicando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao publicar o modelo.');
      },
    });
  }

  adicionarAtividade(pr: ModeloPratica): void {
    this.dialog.open(AtividadeFormDialog, {
      width: '560px',
      data: { modCod: this.modCod, prtCod: pr.prtCod, pratica: pr.nome },
    });
  }

  editarAtividade(pr: ModeloPratica, atv: AtividadeModelo): void {
    this.dialog.open(AtividadeFormDialog, {
      width: '560px',
      data: { modCod: this.modCod, prtCod: pr.prtCod, pratica: pr.nome, atividade: atv },
    });
  }

  alternarAtividade(atv: AtividadeModelo): void {
    const situacao = atv.situacao === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    this.service
      .alterarSituacaoAtividade(this.modCod, atv.atmCod, situacao)
      .subscribe(() => this.service.recarregar());
  }
}
