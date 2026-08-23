import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { AuthService } from '../services/auth/auth.service';
import { MinhaIncubadoraService } from '../services/minha-incubadora/minha-incubadora.service';
import { ENivelIncubadora, NIVEL_LABEL } from '../../models/incubadora/incubadora.model';
import { IncubadoraSobreDialog } from './incubadora-sobre.dialog';

/** Casca da incubadora: mesmo estilo do admin, mas o rodapé traz o nome da incubadora e o nível CERNE. */
@Component({
  selector: 'app-incubadora-layout',
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
  ],
  templateUrl: './incubadora-layout.component.html',
  styleUrl: './incubadora-layout.component.css',
})
export class IncubadoraLayoutComponent {
  private readonly service = inject(MinhaIncubadoraService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);

  /** Alimenta o rodapé (nome + nível). */
  readonly incubadora = rxResource({ stream: () => this.service.buscar() });

  nivelLabel(n: ENivelIncubadora): string {
    return NIVEL_LABEL[n];
  }

  /** Abre a ficha institucional (pouco consultada) num modal, com a incubadora já carregada. */
  sobre(): void {
    const inc = this.incubadora.value();
    if (!inc) {
      return;
    }
    this.dialog.open(IncubadoraSobreDialog, { width: '520px', data: { incubadora: inc } });
  }

  sair(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
