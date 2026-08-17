import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { NgClass } from '@angular/common';
import { IncubadoraService } from '../../core/services/incubadora/incubadora.service';
import { EStatusIncubadora, IncubadoraResumo, STATUS_LABEL } from '../../models/incubadora/incubadora.model';
import { IncubadoraConfigurarDialog } from './incubadora-configurar.dialog';
import { IncubadoraConsultarDialog } from './incubadora-consultar.dialog';

/** Tela "Incubadoras" do administrador: listagem, filtro e ações. */
@Component({
  selector: 'app-incubadoras-list',
  imports: [
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    NgClass
  ],
  templateUrl: './incubadoras-list.component.html',
  styleUrl: './incubadoras-list.component.css',
})
export class IncubadorasListComponent {
  private readonly service = inject(IncubadoraService);
  private readonly dialog = inject(MatDialog);

  readonly nome = signal('');
  readonly status = signal('');
  readonly colunas = ['nome', 'mantenedora', 'responsavel', 'usuarios', 'status', 'acoes'];

  /**
   * Resource reativo sobre a service comum: refaz a busca sempre que o nome, o status
   * ou a {@code versao} (mutações: ativar/suspender/salvar) mudam.
   */
  readonly dados = rxResource({
    params: () => ({
      versao: this.service.versao(),
      nome: this.nome().trim(),
      status: this.status(),
    }),
    stream: ({ params }) => this.service.listar(params.nome, params.status),
  });

  label(s: EStatusIncubadora): string {
    return STATUS_LABEL[s];
  }

  nova(): void {
    this.dialog.open(IncubadoraConfigurarDialog, { data: null, width: '640px' });
  }

  consultar(i: IncubadoraResumo): void {
    this.dialog.open(IncubadoraConsultarDialog, { data: i.incCod, width: '560px' });
  }

  configurar(i: IncubadoraResumo): void {
    this.dialog.open(IncubadoraConfigurarDialog, { data: i.incCod, width: '640px' });
  }

  alternar(i: IncubadoraResumo): void {
    this.service.alternarStatus(i.incCod).subscribe(() => this.service.recarregar());
  }
}
