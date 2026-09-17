import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { IncubadoraService } from '../../core/services/incubadora/incubadora.service';
import { IncubadoraDetalhe } from '../../models/incubadora/incubadora.model';

@Component({
  selector: 'app-incubadora-configurar',
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './incubadora-configurar.dialog.html',
  styleUrl: './incubadora-configurar.dialog.css',
})
export class IncubadoraConfigurarDialog {
  private readonly service = inject(IncubadoraService);
  private readonly ref = inject(MatDialogRef<IncubadoraConfigurarDialog>);
  readonly id = inject<number | null>(MAT_DIALOG_DATA);
  readonly novo = this.id === null;
  readonly carregado = signal(false);
  readonly salvando = signal(false);
  dados: IncubadoraDetalhe | null = null;

  constructor() {
    if (this.id === null) {
      this.dados = this.vazia();
      this.carregado.set(true);
    } else {
      this.service.buscar(this.id).subscribe(d => {
        this.dados = d;
        this.carregado.set(true);
      });
    }
  }

  salvar(): void {
    if (!this.dados) return;
    this.salvando.set(true);
    const requisicao =
      this.id === null ? this.service.criar(this.dados) : this.service.salvar(this.id, this.dados);
    requisicao.subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: () => this.salvando.set(false),
    });
  }

  private vazia(): IncubadoraDetalhe {
    return {
      incCod: 0,
      nome: '',
      cnpj: null,
      mantenedora: null,
      respCtaCod: null,
      responsavelNome: null,
      email: null,
      telefone: null,
      cidade: null,
      nivel: null,
      status: 'AGUARDANDO_ATIVACAO',
      nomeSchema: null,
      criadaEm: null,
      ativadaEm: null,
    };
  }
}
