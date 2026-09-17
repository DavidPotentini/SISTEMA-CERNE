import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { EmpreendimentoService } from '../../core/services/empreendimento/empreendimento.service';
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
import { EmpreendimentoGerenciarDialog } from './empreendimento-gerenciar.dialog';
import { PessoasEmpreendimentoDialog } from './pessoas-empreendimento.dialog';
import { reterRecurso } from '../../shared/util/reter-recurso';

@Component({
  selector: 'app-empreendimentos-list',
  imports: [
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatProgressBarModule,
    MatTooltipModule,
  ],
  templateUrl: './empreendimentos-list.component.html',
  styleUrl: './empreendimentos-list.component.css',
})
export class EmpreendimentosListComponent {
  private readonly service = inject(EmpreendimentoService);
  private readonly dialog = inject(MatDialog);

  readonly colunas = [
    'nome',
    'cnpj',
    'cnae',
    'atividadeEconomica',
    'estagio',
    'nivelMaturidade',
    'situacaoContrato',
    'periodo',
    'contatos',
    'status',
    'pessoas',
    'acoes',
  ];

  readonly dados = reterRecurso(rxResource({
    params: () => ({ versao: this.service.versao() }),
    stream: () => this.service.listar(),
  }));

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

  fmtData(data: string | null): string {
    return data ? data.split('-').reverse().join('/') : '—';
  }

  periodo(e: Empreendimento): string {
    if (!e.entrada && !e.saida) return '—';
    return `${this.fmtData(e.entrada)} — ${this.fmtData(e.saida)}`;
  }

  siteUrl(site: string): string {
    return /^https?:\/\//i.test(site) ? site : `https://${site}`;
  }

  instagramUrl(instagram: string): string {
    const handle = instagram.replace(/^@/, '').replace(/^https?:\/\/(www\.)?instagram\.com\//i, '');
    return `https://instagram.com/${handle}`;
  }

  adicionar(): void {
    this.dialog.open(EmpreendimentoGerenciarDialog, { data: null, width: '90vw', maxWidth: '1200px' });
  }

  editar(e: Empreendimento): void {
    this.dialog.open(EmpreendimentoGerenciarDialog, { data: e, width: '90vw', maxWidth: '1200px' });
  }

  verPessoas(e: Empreendimento): void {
    this.dialog.open(PessoasEmpreendimentoDialog, { data: { empCod: e.empCod, nome: e.nome } });
  }
}
