import { Component, effect, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { of } from 'rxjs';
import { IncubadoraService } from '../../core/services/incubadora/incubadora.service';
import { UsuarioService } from '../../core/services/usuario/usuario.service';
import { UsuarioResumo } from '../../models/usuario/usuario.model';

@Component({
  selector: 'app-usuario-form',
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './usuario-form.dialog.html',
  styleUrl: './usuario-form.dialog.css',
})
export class UsuarioFormDialog {
  private readonly service = inject(UsuarioService);
  private readonly incubadoraService = inject(IncubadoraService);
  private readonly ref = inject(MatDialogRef<UsuarioFormDialog>);
  readonly usuario = inject<UsuarioResumo | null>(MAT_DIALOG_DATA);
  readonly novo = this.usuario === null;

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  nome = signal(this.usuario?.nome ?? '');
  email = signal(this.usuario?.email ?? '');
  readonly incCod = signal<number | null>(this.usuario?.incCod ?? null);
  readonly papCod = signal<number | null>(this.usuario?.papCod ?? null);

  readonly incubadoras = rxResource({
    stream: () => this.incubadoraService.listar(),
  });

  readonly papeis = rxResource({
    params: () => ({ incCod: this.incCod() }),
    stream: ({ params }) =>
      params.incCod == null ? of([]) : this.service.listarPapeis(params.incCod),
  });

  constructor() {
    // Ao trocar de incubadora, o papel anterior deixa de valer.
    let primeiro = true;
    effect(() => {
      this.incCod();
      if (primeiro) {
        primeiro = false;
        return;
      }
      this.papCod.set(null);
    });
  }

  salvar(): void {
    if (!this.nome().trim()) return;
    if (this.novo && !this.email().trim()) return;
    this.salvando.set(true);
    this.erro.set(null);
    const requisicao = this.novo
      ? this.service.convidar({
          nome: this.nome().trim(),
          email: this.email().trim(),
          incCod: this.incCod(),
          papCod: this.papCod(),
        })
      : this.service.editar(this.usuario!.ctaCod, {
          nome: this.nome().trim(),
          incCod: this.incCod(),
          papCod: this.papCod(),
        });
    requisicao.subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao salvar o usuário.');
      },
    });
  }
}
