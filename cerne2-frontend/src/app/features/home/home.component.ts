import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: 'home.component.html',
  styleUrls: ['home.component.css'],
  standalone: true,
  imports: [CommonModule, RouterLink],
})
export class HomeComponent {
  private readonly sessao = inject(AuthService).getSessao();

  readonly nome = this.sessao?.nome ?? '';
  readonly nomeEmpresa = this.sessao?.nomeEmpresa ?? '';
  readonly incCod = this.sessao?.incCod ?? null;
  readonly ehIncubada = this.incCod != null;
}
