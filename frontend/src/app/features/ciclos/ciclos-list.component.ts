import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { CicloService } from '../../core/services/ciclo/ciclo.service';
import { Ciclo, EStatusCiclo, STATUS_CICLO_LABEL } from '../../models/ciclo/ciclo.model';
import { CicloFormDialog } from './ciclo-form.dialog';
import { reterRecurso } from '../../shared/util/reter-recurso';

@Component({
  selector: 'app-ciclos-list',
  imports: [MatCardModule, MatTableModule, MatButtonModule, MatDialogModule],
  templateUrl: './ciclos-list.component.html',
  styleUrl: './ciclos-list.component.css',
})
export class CiclosListComponent {
  private readonly service = inject(CicloService);
  private readonly dialog = inject(MatDialog);

  readonly colunas = ['nome', 'periodo', 'status', 'acoes'];

  readonly erroEncerrar = signal<string | null>(null);

  readonly dados = reterRecurso(rxResource({
    params: () => ({ versao: this.service.versao() }),
    stream: () => this.service.listar(),
  }));

  statusLabel(s: EStatusCiclo): string {
    return STATUS_CICLO_LABEL[s];
  }

  /** Formata sem Date para não sofrer com timezone. */
  periodo(c: Ciclo): string {
    const f = (d: string | null) => (d ? d.split('-').reverse().join('/') : null);
    const ini = f(c.inicio);
    const fim = f(c.fim);
    if (ini && fim) return `${ini} – ${fim}`;
    if (ini) return `desde ${ini}`;
    if (fim) return `até ${fim}`;
    return '—';
  }

  adicionar(): void {
    this.dialog.open(CicloFormDialog, { width: '90vw', maxWidth: '1200px' });
  }

  porEmFoco(c: Ciclo): void {
    this.service.porEmFoco(c.cicCod).subscribe(() => this.service.recarregar());
  }

  encerrar(c: Ciclo): void {
    const ok = window.confirm(
      `Encerrar o ciclo "${c.nome}"? Esta ação é irreversível: o ciclo passa a somente leitura.`,
    );
    if (!ok) return;
    this.erroEncerrar.set(null);
    this.service.encerrar(c.cicCod).subscribe({
      next: () => this.service.recarregar(),
      error: e => this.erroEncerrar.set(e?.error?.mensagem ?? 'Falha ao encerrar o ciclo.'),
    });
  }
}
