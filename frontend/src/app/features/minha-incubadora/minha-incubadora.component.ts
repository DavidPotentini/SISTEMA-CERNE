import { Component } from '@angular/core';
import { CiclosListComponent } from '../ciclos/ciclos-list.component';
import { EmpreendimentosListComponent } from '../empreendimentos/empreendimentos-list.component';
import { EquipeCardComponent } from './equipe-card.component';
import { IncubadoraDadosCardComponent } from './incubadora-dados-card.component';

/**
 * Tela "Minha Incubadora": apenas o layout. Compõe cards autossuficientes (dados, equipe,
 * empreendimentos), cada um dono do próprio dado — carregam, erram e recarregam de forma
 * independente. Adicionar um novo quadro = criar o componente e soltar a tag aqui.
 */
@Component({
  selector: 'app-minha-incubadora',
  imports: [
    IncubadoraDadosCardComponent,
    EquipeCardComponent,
    CiclosListComponent,
    EmpreendimentosListComponent,
  ],
  templateUrl: './minha-incubadora.component.html',
  styleUrl: './minha-incubadora.component.css',
})
export class MinhaIncubadoraComponent {}
