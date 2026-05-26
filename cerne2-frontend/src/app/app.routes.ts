import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login.component';
import { CadastroComponent } from './features/auth/cadastro.component';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { Role } from './models/auth/auth.model';
import { PlanejamentoComponent } from './features/planejamento/planejamento-list/planejamento.component';
import { PlanejamentoDetalheComponent } from './features/planejamento/planejamento-list/planejamento-detalhe.component';
import { ProjetoComponent } from './features/planejamento/projeto-list/projeto.component';
import { ProjetoDetalheComponent } from './features/planejamento/projeto-list/projeto-detalhe.component';
import { ObjetivoComponent } from './features/planejamento/objetivo-list/objetivo.component';
import { ObjetivoDetalheComponent } from './features/planejamento/objetivo-list/objetivo-detalhe.component';
import { TarefaComponent } from './features/planejamento/tarefa-list/tarefa.component';
import { TarefaDetalheComponent } from './features/planejamento/tarefa-list/tarefa-detalhe.component';
import { ServicoValorAgregadoListComponent } from './features/servicos-valor-agregado/servico-valor-agregado-list.component';
import { ServicoValorAgregadoDetalheComponent } from './features/servicos-valor-agregado/servico-valor-agregado-detalhe.component';
import { PessoaListComponent } from './features/pessoas/pessoa-list.component';
import { PessoaDetalheComponent } from './features/pessoas/pessoa-detalhe.component';
import { IncubadaListComponent } from './features/incubadas/incubada-list.component';
import { IncubadaDetalheComponent } from './features/incubadas/incubada-detalhe.component';
import { MetricaDetalheComponent } from './features/metricas/metrica-detalhe.component';
import { MetricaListComponent } from './features/metricas/metrica-list.component';
import { FormularioListComponent } from './features/formularios/formulario-list.component';
import { FormularioDetalheComponent } from './features/formularios/formulario-detalhe.component';
import { AmbienteCanvasListComponent } from './features/canvas/ambiente-canvas-list.component';
import { AmbienteCanvasDetalheComponent } from './features/canvas/ambiente-canvas-detalhe.component';
import { QuadroListComponent } from './features/validacao-hipotese/quadro-list.component';
import { QuadroDetalheComponent } from './features/validacao-hipotese/quadro-detalhe.component';

// Filhas reaproveitadas pelos três prefixos de planejamento (incubadora / incubada direta /
// monitoramento via gerência). Mantidas em uma única constante para que mudanças na árvore
// (novos níveis, renomeações) refletem em todos os pontos de entrada sem duplicação.
const planejamentoChildren: Routes = [
  { path: '', component: PlanejamentoComponent },
  { path: ':pesCod', component: PlanejamentoDetalheComponent },
  { path: ':pesCod/projetos', component: ProjetoComponent },
  { path: ':pesCod/projetos/:prjCod', component: ProjetoDetalheComponent },
  { path: ':pesCod/projetos/:prjCod/objetivos', component: ObjetivoComponent },
  { path: ':pesCod/projetos/:prjCod/objetivos/:objCod', component: ObjetivoDetalheComponent },
  { path: ':pesCod/projetos/:prjCod/objetivos/:objCod/tarefas', component: TarefaComponent },
  { path: ':pesCod/projetos/:prjCod/objetivos/:objCod/tarefas/:trfCod', component: TarefaDetalheComponent },
];

// Atalhos para os conjuntos de papéis usados nas rotas (espelha SecurityConfig do backend).
const AMBOS: Role[] = ['ADMIN_INCUBADORA', 'ADMIN_INCUBADA'];
const SO_INCUBADORA: Role[] = ['ADMIN_INCUBADORA'];
const SO_INCUBADA: Role[] = ['ADMIN_INCUBADA'];

// Rotas internas — exigem sessão ativa (authGuard) e papel compatível (roleGuard).
const rotasInternas: Routes = [
  // Planejamento da própria incubadora
  { path: 'planejamento', children: planejamentoChildren, data: { roles: AMBOS } },

  // Planejamento de uma incubada (leitura direta via prefixo de incubada)
  { path: ':incCod/planejamento', children: planejamentoChildren, data: { roles: AMBOS } },

  // Planejamento de uma incubada no modo monitoramento/avaliação dentro da gerência
  {
    path: 'gerenciaIncubadas/:incCod/planejamento',
    children: planejamentoChildren,
    data: { modoAvaliacao: true, roles: SO_INCUBADORA },
  },

  // Serviços de Valor Agregado
  { path: 'servicosValorAgregado', component: ServicoValorAgregadoListComponent, data: { roles: SO_INCUBADORA } },
  { path: 'servicosValorAgregado/:servCod', component: ServicoValorAgregadoDetalheComponent, data: { roles: SO_INCUBADORA } },

  // Pessoas
  { path: 'pessoas', component: PessoaListComponent, data: { roles: SO_INCUBADORA } },
  { path: 'pessoas/:pessoaCod', component: PessoaDetalheComponent, data: { roles: SO_INCUBADORA } },

  // Incubadas (lista e detalhe são acessíveis a ambos os papéis no backend)
  { path: 'incubadas', component: IncubadaListComponent, data: { roles: AMBOS } },
  { path: 'incubadas/:incCod', component: IncubadaDetalheComponent, data: { roles: AMBOS } },

  // Métricas
  { path: 'metricas', component: MetricaListComponent, data: { roles: SO_INCUBADORA } },
  { path: 'metricas/:metCod', component: MetricaDetalheComponent, data: { roles: SO_INCUBADORA } },

  // Formulários
  { path: 'formularios', component: FormularioListComponent, data: { roles: SO_INCUBADORA } },
  { path: 'formularios/:frmCod', component: FormularioDetalheComponent, data: { roles: SO_INCUBADORA } },

  // Canvas (por incubada/ambiente)
  { path: 'incubadas/:incCod/ambientesCanvas', component: AmbienteCanvasListComponent, data: { roles: SO_INCUBADA } },
  { path: 'incubadas/:incCod/ambientesCanvas/:ambcCod', component: AmbienteCanvasDetalheComponent, data: { roles: SO_INCUBADA } },

  // Validação de Hipóteses (por incubada/quadro)
  { path: 'incubadas/:incCod/quadrosValidacao', component: QuadroListComponent, data: { roles: SO_INCUBADA } },
  { path: 'incubadas/:incCod/quadrosValidacao/:qvhCod', component: QuadroDetalheComponent, data: { roles: SO_INCUBADA } },
];

export const routes: Routes = [
  // Rotas públicas
  { path: 'login', component: LoginComponent },
  { path: 'cadastro', component: CadastroComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // Rotas internas protegidas
  ...rotasInternas.map(rota => ({ ...rota, canActivate: [authGuard, roleGuard] })),

  { path: '**', redirectTo: 'login' },
];
