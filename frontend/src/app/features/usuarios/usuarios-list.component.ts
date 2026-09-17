import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { UsuarioService } from '../../core/services/usuario/usuario.service';
import { EStatusConta, STATUS_CONTA_LABEL, UsuarioResumo } from '../../models/usuario/usuario.model';
import { UsuarioFormDialog } from './usuario-form.dialog';
import { NgClass } from '@angular/common';
import { reterRecurso } from '../../shared/util/reter-recurso';

@Component({
  selector: 'app-usuarios-list',
  imports: [MatTableModule, MatButtonModule, MatDialogModule, NgClass],
  templateUrl: './usuarios-list.component.html',
  styleUrl: './usuarios-list.component.css',
})
export class UsuariosListComponent {
  private readonly service = inject(UsuarioService);
  private readonly dialog = inject(MatDialog);

  readonly colunas = ['nome', 'email', 'incubadora', 'papel', 'status', 'acoes'];

  readonly dados = reterRecurso(rxResource({
    params: () => ({ versao: this.service.versao() }),
    stream: () => this.service.listar(),
  }));

  label(s: EStatusConta): string {
    return STATUS_CONTA_LABEL[s];
  }

  adicionar(): void {
    this.dialog.open(UsuarioFormDialog, { data: null, width: '90vw', maxWidth: '1200px' });
  }

  editar(u: UsuarioResumo): void {
    this.dialog.open(UsuarioFormDialog, { data: u, width: '90vw', maxWidth: '1200px' });
  }

  alternar(u: UsuarioResumo): void {
    this.service.alternarStatus(u.ctaCod).subscribe(() => this.service.recarregar());
  }
}
