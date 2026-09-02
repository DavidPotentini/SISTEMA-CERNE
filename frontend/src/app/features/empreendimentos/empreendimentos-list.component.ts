import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { EmpreendimentoService } from '../../core/services/empreendimento/empreendimento.service';
import {
  EEstagioIncubacao,
  EStatusEmpreendimento,
  Empreendimento,
  ESTAGIO_LABEL,
  STATUS_EMP_LABEL,
} from '../../models/empreendimento/empreendimento.model';
import { EmpreendimentoGerenciarDialog } from './empreendimento-gerenciar.dialog';
import { reterRecurso } from '../../shared/util/reter-recurso';

/** Tela "Empreendimentos" da incubadora: listagem, adicionar e editar (dados + pessoas). */
@Component({
  selector: 'app-empreendimentos-list',
  imports: [MatCardModule, MatTableModule, MatButtonModule, MatDialogModule],
  templateUrl: './empreendimentos-list.component.html',
  styleUrl: './empreendimentos-list.component.css',
})
export class EmpreendimentosListComponent {
  private readonly service = inject(EmpreendimentoService);
  private readonly dialog = inject(MatDialog);

  readonly colunas = ['nome', 'cnpj', 'estagio', 'status', 'acoes'];

  /** Refaz a busca sempre que houver mutação (criar / editar). */
  readonly dados = reterRecurso(rxResource({
    params: () => ({ versao: this.service.versao() }),
    stream: () => this.service.listar(),
  }));

  estagioLabel(e: EEstagioIncubacao | null): string {
    return e ? ESTAGIO_LABEL[e] : '—';
  }

  statusLabel(s: EStatusEmpreendimento): string {
    return STATUS_EMP_LABEL[s];
  }

  adicionar(): void {
    this.dialog.open(EmpreendimentoGerenciarDialog, { data: null, width: '90vw', maxWidth: '1200px' });
  }

  editar(e: Empreendimento): void {
    this.dialog.open(EmpreendimentoGerenciarDialog, { data: e, width: '90vw', maxWidth: '1200px' });
  }
}
