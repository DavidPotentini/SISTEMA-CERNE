import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatCardModule } from '@angular/material/card';
import { MinhaIncubadoraService } from '../../core/services/minha-incubadora/minha-incubadora.service';
import {
  ENivelIncubadora,
  EStatusIncubadora,
  NIVEL_LABEL,
  STATUS_LABEL,
} from '../../models/incubadora/incubadora.model';

/** Card de dados da incubadora do usuário logado. Carrega o próprio dado, independente. */
@Component({
  selector: 'app-incubadora-dados-card',
  imports: [MatCardModule],
  templateUrl: './incubadora-dados-card.component.html',
  styleUrl: './incubadora-dados-card.component.css',
})
export class IncubadoraDadosCardComponent {
  private readonly service = inject(MinhaIncubadoraService);

  readonly dados = rxResource({ stream: () => this.service.buscar() });

  label(s: EStatusIncubadora): string {
    return STATUS_LABEL[s];
  }

  nivelLabel(n: ENivelIncubadora): string {
    return NIVEL_LABEL[n];
  }
}
