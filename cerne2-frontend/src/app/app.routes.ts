import { Routes } from '@angular/router';
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

export const routes: Routes = [
  // Planejamento
  { path: 'planejamento', component: PlanejamentoComponent },
  { path: 'planejamento/:pesCod', component: PlanejamentoDetalheComponent },

  // Projetos
  { path: ':pesCod/projetos', component: ProjetoComponent },
  { path: ':pesCod/projetos/:prjCod', component: ProjetoDetalheComponent },

  // Objetivos
  { path: ':pesCod/projetos/:prjCod/objetivos', component: ObjetivoComponent },
  { path: ':pesCod/projetos/:prjCod/objetivos/:objCod', component: ObjetivoDetalheComponent },

  // Tarefas
  { path: ':pesCod/projetos/:prjCod/objetivos/:objCod/tarefas', component: TarefaComponent },
  { path: ':pesCod/projetos/:prjCod/objetivos/:objCod/tarefas/:trfCod', component: TarefaDetalheComponent },

  // Serviços de Valor Agregado
  { path: 'servicosValorAgregado', component: ServicoValorAgregadoListComponent },
  { path: 'servicosValorAgregado/:servCod', component: ServicoValorAgregadoDetalheComponent },

  // Pessoas
  { path: 'pessoas', component: PessoaListComponent },
  { path: 'pessoas/:pessoaCod', component: PessoaDetalheComponent },

  // Incubadas
  { path: 'incubadas', component: IncubadaListComponent },
  { path: 'incubadas/:incCod', component: IncubadaDetalheComponent },

  // Métricas
  { path: 'metricas', component: MetricaListComponent },
  { path: 'metricas/:metCod', component: MetricaDetalheComponent },

  // Formulários
  { path: 'formularios', component: FormularioListComponent },
  { path: 'formularios/:frmCod', component: FormularioDetalheComponent },
];
