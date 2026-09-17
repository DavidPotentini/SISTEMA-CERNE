import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-proximo-passo',
  imports: [RouterLink, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './proximo-passo.component.html',
  styleUrl: './proximo-passo.component.css',
})
export class ProximoPassoComponent {
  readonly icone = input('info');
  readonly rotulo = input.required<string>();
  readonly rota = input.required<string>();
}
