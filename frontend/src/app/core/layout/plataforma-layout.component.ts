import { Component, inject } from '@angular/core';
import { httpResource } from '@angular/common/http';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { environment } from '../../../environments/environment';
import { AuthService } from '../services/auth/auth.service';
import { IncubadoraService } from '../services/incubadora/incubadora.service';

@Component({
  selector: 'app-plataforma-layout',
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
  ],
  templateUrl: './plataforma-layout.component.html',
  styleUrl: './plataforma-layout.component.css',
})
export class PlataformaLayoutComponent {
  private readonly service = inject(IncubadoraService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  /** `versao()` reconsulta a contagem a cada mutação (ativar/suspender). */
  readonly ativas = httpResource<number>(() => {
    this.service.versao();
    return `${environment.apiUrl}/admin/incubadoras/contagem-ativas`;
  });

  sair(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
