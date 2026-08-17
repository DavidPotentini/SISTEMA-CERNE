import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent),
  },
  { path: '', pathMatch: 'full', redirectTo: 'admin/painel' },
  {
    path: 'admin',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./core/layout/plataforma-layout.component').then(m => m.PlataformaLayoutComponent),
    children: [
      {
        path: 'painel',
        loadComponent: () =>
          import('./features/painel/painel.component').then(m => m.PainelComponent),
      },
      {
        path: 'incubadoras',
        loadComponent: () =>
          import('./features/incubadoras/incubadoras-list.component').then(
            m => m.IncubadorasListComponent,
          ),
      },
      {
        path: 'usuarios',
        loadComponent: () =>
          import('./features/usuarios/usuarios-list.component').then(m => m.UsuariosListComponent),
      },
      { path: '', pathMatch: 'full', redirectTo: 'painel' },
    ],
  },
  {
    path: 'incubadora',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./core/layout/incubadora-layout.component').then(m => m.IncubadoraLayoutComponent),
    children: [
      {
        path: 'minha-incubadora',
        loadComponent: () =>
          import('./features/minha-incubadora/minha-incubadora.component').then(
            m => m.MinhaIncubadoraComponent,
          ),
      },
      {
        path: 'metodologia',
        loadComponent: () =>
          import('./features/metodologia/metodologia.component').then(m => m.MetodologiaComponent),
      },
      {
        path: 'modelos',
        loadComponent: () =>
          import('./features/modelos-planejamento/modelos.component').then(m => m.ModelosComponent),
      },
      {
        path: 'modelos/:modCod',
        loadComponent: () =>
          import('./features/modelos-planejamento/modelo-editor.component').then(
            m => m.ModeloEditorComponent,
          ),
      },
      {
        path: 'planejamento',
        loadComponent: () =>
          import('./features/planejamento/planejamento.component').then(
            m => m.PlanejamentoComponent,
          ),
      },
      {
        path: 'evidencias',
        loadComponent: () =>
          import('./features/evidencias/evidencias.component').then(m => m.EvidenciasComponent),
      },
      {
        path: 'acompanhamento',
        loadComponent: () =>
          import('./features/acompanhamento/acompanhamento.component').then(
            m => m.AcompanhamentoComponent,
          ),
      },
      {
        path: 'indicadores',
        loadComponent: () =>
          import('./features/indicadores/indicadores.component').then(m => m.IndicadoresComponent),
      },
      {
        path: 'apuracao',
        loadComponent: () =>
          import('./features/apuracao/apuracao.component').then(m => m.ApuracaoComponent),
      },
      {
        path: 'monitoramento',
        loadComponent: () =>
          import('./features/monitoramento/monitoramento.component').then(
            m => m.MonitoramentoComponent,
          ),
      },
      { path: '', pathMatch: 'full', redirectTo: 'minha-incubadora' },
    ],
  },
];
