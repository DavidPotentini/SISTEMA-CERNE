import { Component, computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { of } from 'rxjs';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import { Pratica, Processo } from '../../models/metodologia/metodologia.model';
import { ProcessoFormDialog } from './processo-form.dialog';
import { PraticaFormDialog } from './pratica-form.dialog';

/**
 * Aba "Processos e Práticas": accordions ordenados por `ordem`. Ao expandir, veem-se as práticas e
 * o botão de adicionar prática àquele processo. No topo, adicionar novo processo. Opera sobre a
 * versão vigente (buscada uma vez; o verCod alimenta a listagem).
 */
@Component({
  selector: 'app-processos-tab',
  imports: [MatExpansionModule, MatButtonModule, MatIconModule, MatDialogModule, MatTooltipModule],
  templateUrl: './processos-tab.component.html',
  styleUrl: './processos-tab.component.css',
})
export class ProcessosTabComponent {
  private readonly service = inject(MetodologiaService);
  private readonly dialog = inject(MatDialog);

  /** Versão de trabalho (rascunho, criado no backend na primeira vez). */
  readonly versaoRes = rxResource({ stream: () => this.service.versaoDeTrabalho() });
  readonly verCod = computed(() => this.versaoRes.value()?.verCod ?? null);

  /** Processos da versão; refaz a busca quando o verCod chega ou há mutação. */
  readonly processos = rxResource({
    params: () => ({ verCod: this.verCod(), v: this.service.versao() }),
    stream: ({ params }) =>
      params.verCod == null ? of<Processo[]>([]) : this.service.listarProcessos(params.verCod),
  });

  adicionarProcesso(): void {
    const verCod = this.verCod();
    if (verCod == null) return;
    this.dialog.open(ProcessoFormDialog, { width: '520px', data: { verCod } });
  }

  editarProcesso(p: Processo): void {
    this.dialog.open(ProcessoFormDialog, { width: '520px', data: { verCod: p.verCod, processo: p } });
  }

  alternarProcesso(p: Processo): void {
    const situacao = p.situacao === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    this.service.alterarSituacaoProcesso(p.prcCod, situacao).subscribe(() => this.service.recarregar());
  }

  adicionarPratica(p: Processo): void {
    this.dialog.open(PraticaFormDialog, { width: '520px', data: { prcCod: p.prcCod, processo: p.nome } });
  }

  editarPratica(p: Processo, pr: Pratica): void {
    this.dialog.open(PraticaFormDialog, {
      width: '520px',
      data: { prcCod: p.prcCod, processo: p.nome, pratica: pr },
    });
  }

  alternarPratica(p: Processo, pr: Pratica): void {
    const situacao = pr.situacao === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    this.service
      .alterarSituacaoPratica(p.prcCod, pr.prtCod, situacao)
      .subscribe(() => this.service.recarregar());
  }
}
