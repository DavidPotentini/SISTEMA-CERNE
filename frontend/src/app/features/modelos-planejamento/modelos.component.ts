import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { Router } from '@angular/router';
import { ModelosService } from '../../core/services/modelos/modelos.service';
import { EPeriodicidade, PERIODICIDADE_LABEL } from '../../models/metodologia/metodologia.model';
import { EStatusModelo, Modelo, STATUS_MODELO_LABEL } from '../../models/modelos/modelo.model';
import { ModeloFormDialog } from './modelo-form.dialog';

/**
 * Tela "Modelos de planejamento": lista os modelos (nome, periodicidade, versão da metodologia,
 * status, publicação) com "Novo modelo" no topo. Clicar num modelo abre o editor.
 */
@Component({
  selector: 'app-modelos',
  imports: [MatTableModule, MatButtonModule, MatIconModule, MatDialogModule],
  templateUrl: './modelos.component.html',
  styleUrl: './modelos.component.css',
})
export class ModelosComponent {
  private readonly service = inject(ModelosService);
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);

  readonly colunas = ['nome', 'periodicidade', 'status', 'publicado'];

  readonly modelos = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listarModelos(),
  });

  periodicidadeLabel(p: EPeriodicidade): string {
    return PERIODICIDADE_LABEL[p];
  }
  statusLabel(s: EStatusModelo): string {
    return STATUS_MODELO_LABEL[s];
  }

  novo(): void {
    this.dialog
      .open(ModeloFormDialog, { width: '560px', data: {} })
      .afterClosed()
      .subscribe((criado?: Modelo) => {
        if (criado) this.abrir(criado);
      });
  }

  abrir(m: Modelo): void {
    this.router.navigate(['/incubadora/modelos', m.modCod]);
  }

  dataPub(m: Modelo): string {
    return m.publicadoEm ? new Date(m.publicadoEm).toLocaleDateString('pt-BR') : '—';
  }
}
