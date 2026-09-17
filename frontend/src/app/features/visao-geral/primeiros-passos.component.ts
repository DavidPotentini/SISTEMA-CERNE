import { Component, computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { EmpreendimentoService } from '../../core/services/empreendimento/empreendimento.service';
import { IndicadorService } from '../../core/services/indicador/indicador.service';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import { PlanejamentoService } from '../../core/services/planejamento/planejamento.service';
import { VisaoGeralService } from '../../core/services/painel/visao-geral.service';
import { CicloReadonlyBannerComponent } from '../ciclos/ciclo-readonly-banner.component';

interface Passo {
  titulo: string;
  desc: string;
  rota: string;
  feito: boolean;
}

@Component({
  selector: 'app-primeiros-passos',
  imports: [
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    CicloReadonlyBannerComponent,
  ],
  templateUrl: './primeiros-passos.component.html',
  styleUrl: './primeiros-passos.component.css',
})
export class PrimeirosPassosComponent {
  private readonly visaoGeral = inject(VisaoGeralService);
  private readonly metodologia = inject(MetodologiaService);
  private readonly empService = inject(EmpreendimentoService);
  private readonly planService = inject(PlanejamentoService);
  private readonly indicadorService = inject(IndicadorService);

  private readonly resumoRes = rxResource({
    stream: () => this.visaoGeral.resumo(),
  });
  private readonly atividadesRes = rxResource({
    stream: () => this.metodologia.listarAtividades(),
  });
  private readonly empresasRes = rxResource({
    params: () => ({ v: this.empService.versao() }),
    stream: () => this.empService.listar(),
  });
  private readonly estruturaRes = rxResource({
    stream: () => this.planService.estrutura(),
  });
  private readonly indicadoresRes = rxResource({
    params: () => ({ v: this.indicadorService.versao() }),
    stream: () => this.indicadorService.listar(),
  });

  readonly passos = computed<Passo[]>(() => {
    const r = this.resumoRes.value() ?? null;
    const empresas = this.empresasRes.value() ?? [];
    const temMetodologia = (this.atividadesRes.value()?.length ?? 0) > 0;
    const temEmpreendimento = empresas.length > 0;
    const temVinculo = empresas.some(e => e.cicCod != null);
    const temCiclo = r?.cicloNome != null;
    const planoGerado = (r?.atividadesTotal ?? 0) > 0;
    const temMetas = (r?.indicadoresComMeta ?? 0) > 0;
    const execucao = (r?.atividadesConcluidas ?? 0) > 0;
    const estrutura = this.estruturaRes.value() ?? [];
    const planejamentoAjustado = estrutura.some(p =>
      p.praticas.some(pr =>
        pr.grupos.some(g => g.atividades.some(a => a.respPesCod != null || a.prazo != null)),
      ),
    );
    const indicadores = this.indicadoresRes.value() ?? [];
    const indicadoresConfigurados =
      indicadores.length > 0 && indicadores.every(i => i.respPesCod != null) && temMetas;
    return [
      { titulo: 'Definir a metodologia', desc: 'Monte a árvore de processos, práticas e atividades.', rota: '/incubadora/metodologia', feito: temMetodologia },
      { titulo: 'Abrir um ciclo e pôr em foco', desc: 'Crie o ciclo e defina-o como em foco.', rota: '/incubadora/minha-incubadora', feito: temCiclo },
      { titulo: 'Cadastrar empreendimentos', desc: 'Registre os empreendimentos da incubadora.', rota: '/incubadora/empreendimentos', feito: temEmpreendimento },
      { titulo: 'Vincular empreendimentos ao ciclo', desc: 'No cadastro, selecione o ciclo do empreendimento.', rota: '/incubadora/empreendimentos', feito: temVinculo },
      { titulo: 'Gerar o ciclo', desc: 'Publique a metodologia: gera estrutura, indicadores e planejamento.', rota: '/incubadora/metodologia', feito: planoGerado },
      { titulo: 'Ajustar o planejamento', desc: 'Defina responsáveis e prazos das atividades do ciclo.', rota: '/incubadora/planejamento', feito: planejamentoAjustado },
      { titulo: 'Definir responsáveis e metas dos indicadores', desc: 'Atribua um responsável a cada um e defina as metas.', rota: '/incubadora/indicadores', feito: indicadoresConfigurados },
      { titulo: 'Acompanhar a execução', desc: 'Atualize o status das atividades e valide evidências.', rota: '/incubadora/acompanhamento', feito: execucao },
    ];
  });

  readonly concluidos = computed(() => this.passos().filter(p => p.feito).length);
  readonly proximo = computed(() => this.passos().findIndex(p => !p.feito));
  readonly completo = computed(() => this.proximo() === -1);
}
