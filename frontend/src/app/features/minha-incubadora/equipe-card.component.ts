import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { EquipeService } from '../../core/services/equipe/equipe.service';
import { EStatusConta, STATUS_CONTA_LABEL } from '../../models/usuario/usuario.model';

/** Card da equipe vinculada da incubadora. Carrega o próprio dado, independente. */
@Component({
  selector: 'app-equipe-card',
  imports: [MatCardModule, MatTableModule],
  templateUrl: './equipe-card.component.html',
  styleUrl: './equipe-card.component.css',
})
export class EquipeCardComponent {
  private readonly service = inject(EquipeService);

  readonly equipe = rxResource({ stream: () => this.service.listar() });

  readonly colunas = ['nome', 'email', 'papel', 'situacao'];

  situacaoLabel(s: EStatusConta): string {
    return STATUS_CONTA_LABEL[s];
  }
}
