import { Component } from '@angular/core';
import { CiclosListComponent } from '../ciclos/ciclos-list.component';
import { EquipeCardComponent } from './equipe-card.component';

@Component({
  selector: 'app-minha-incubadora',
  imports: [
    EquipeCardComponent,
    CiclosListComponent,
  ],
  templateUrl: './minha-incubadora.component.html',
  styleUrl: './minha-incubadora.component.css',
})
export class MinhaIncubadoraComponent {}
