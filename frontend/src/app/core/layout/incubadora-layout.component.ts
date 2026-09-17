import { Component, computed, inject, signal, viewChild } from '@angular/core';
import { rxResource, toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter, map } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavContainer, MatSidenavModule } from '@angular/material/sidenav';
import { AuthService } from '../services/auth/auth.service';
import { MinhaIncubadoraService } from '../services/minha-incubadora/minha-incubadora.service';
import { ENivelIncubadora, NIVEL_LABEL } from '../../models/incubadora/incubadora.model';
import { IncubadoraSobreDialog } from './incubadora-sobre.dialog';

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

  readonly incubadora = rxResource({ stream: () => this.service.buscar() });

  private readonly rotasConfig = new Set(['metodologia', 'minha-incubadora', 'empreendimentos']);

  private readonly urlAtual = toSignal(
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd),
      map(e => e.urlAfterRedirects),
    ),
    { initialValue: this.router.url },
  );

  readonly modo = computed<'principal' | 'config'>(() => {
    const seg = this.urlAtual().split('?')[0].split('/incubadora/')[1]?.split('/')[0] ?? '';
    return this.rotasConfig.has(seg) ? 'config' : 'principal';
  });

  readonly recolhido = signal(false);

  private readonly container = viewChild.required(MatSidenavContainer);

  ajustarMargens(): void {
    this.container().updateContentMargins();
  }

  nivelLabel(n: ENivelIncubadora): string {
    return NIVEL_LABEL[n];
  }

  sobre(): void {
    const inc = this.incubadora.value();
    if (!inc) {
      return;
    }
    this.dialog
      .open(IncubadoraSobreDialog, { width: '90vw', maxWidth: '1200px', data: { incubadora: inc } })
      .afterClosed()
      .subscribe(salvou => {
        if (salvou) {
          this.incubadora.reload();
        }
      });
  }

  sair(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
