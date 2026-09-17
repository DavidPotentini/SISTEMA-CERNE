import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { EmpreendimentoService } from '../../core/services/empreendimento/empreendimento.service';

export interface PessoasDialogData {
  empCod: number;
  nome: string;
}

@Component({
  selector: 'app-pessoas-empreendimento',
  imports: [MatDialogModule, MatButtonModule, MatIconModule, MatProgressBarModule],
  templateUrl: './pessoas-empreendimento.dialog.html',
  styleUrl: './pessoas-empreendimento.dialog.css',
})
export class PessoasEmpreendimentoDialog {
  private readonly service = inject(EmpreendimentoService);
  readonly data = inject<PessoasDialogData>(MAT_DIALOG_DATA);

  readonly pessoas = rxResource({
    params: () => ({ empCod: this.data.empCod }),
    stream: ({ params }) => this.service.listarPessoas(params.empCod),
  });
}
