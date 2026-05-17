import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  IndicadorMetricaDTO,
  MetricaDTO,
  QuantidadeMensalMetricaDTO,
} from '../../models/metricas/metrica.model';
import { ObjetivoDTO } from '../../models/planejamento/objetivo.model';
import { PlanejamentoDTO } from '../../models/planejamento/planejamento.model';
import { ProjetoDTO } from '../../models/planejamento/projeto.model';
import { MetricasService } from '../../core/services/metricas/metricas.service';
import { PlanejamentoService } from '../../core/services/planejamento/planajamento.service';

interface EscopoIndicador {
  pesCod: number | null;
  prjCod: number | null;
}

@Component({
  selector: 'app-metrica-detalhe',
  templateUrl: 'metrica-detalhe.component.html',
  styleUrls: ['metrica-detalhe.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule],
})
export class MetricaDetalheComponent implements OnInit, OnDestroy {
  meses = Array.from({ length: 12 }, (_, i) => i + 1);

  form: MetricaDTO = this.formVazio();
  metCod = 0;
  isNovo = true;
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  planos: PlanejamentoDTO[] = [];
  projetosPorPlano = new Map<number, ProjetoDTO[]>();
  objetivosPorProjeto = new Map<number, ObjetivoDTO[]>();
  escopos: EscopoIndicador[] = [];

  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private service: MetricasService,
    private planejamentoService: PlanejamentoService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    const codInicial = this.route.snapshot.paramMap.get('metCod');
    this.isNovo = !codInicial || codInicial === 'novo';

    this.planejamentoService.findAll().subscribe((planos) => {
      this.planos = planos;
      this.cdr.detectChanges();
    });

    this.route.paramMap.subscribe((params) => {
      const cod = params.get('metCod');
      this.isNovo = !cod || cod === 'novo';

      if (this.isNovo) {
        this.form = this.formVazio();
        this.escopos = [];
        this.metCod = 0;
      } else {
        this.metCod = Number(cod);
        this.service.findByMetCod(this.metCod).subscribe((metrica) => {
          this.form = {
            ...metrica,
            indicadoresMetricasDTOList: (metrica.indicadoresMetricasDTOList ?? []).map((i) =>
              this.normalizarIndicador(i),
            ),
          };
          this.popularEscoposParaIndicadores();
        });
      }
    });
  }

  ngOnDestroy() {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  adicionarIndicador() {
    this.form.indicadoresMetricasDTOList.push(this.indicadorVazio());
    this.escopos.push({ pesCod: null, prjCod: null });
  }

  removerIndicador(idx: number) {
    this.form.indicadoresMetricasDTOList.splice(idx, 1);
    this.escopos.splice(idx, 1);
  }

  quantidadeMes(indicador: IndicadorMetricaDTO, mes: number): QuantidadeMensalMetricaDTO {
    let q = indicador.quantidadeMensalMetricasDTOList.find((x) => x.mesCod === mes);
    if (!q) {
      q = { mesCod: mes, quantidade: 0 };
      indicador.quantidadeMensalMetricasDTOList.push(q);
    }
    return q;
  }

  onPlanoChange(idx: number, pesCod: number | null) {
    this.escopos[idx] = { pesCod, prjCod: null };
    this.resetarObjetivo(idx);

    if (pesCod != null && !this.projetosPorPlano.has(pesCod)) {
      this.planejamentoService.findProjetos(pesCod).subscribe((projetos) => {
        this.projetosPorPlano.set(pesCod, projetos);
        this.cdr.detectChanges();
      });
    }
  }

  onProjetoChange(idx: number, prjCod: number | null) {
    this.escopos[idx].prjCod = prjCod;
    this.resetarObjetivo(idx);

    const pesCod = this.escopos[idx].pesCod;
    if (pesCod != null && prjCod != null && !this.objetivosPorProjeto.has(prjCod)) {
      this.planejamentoService.findObjetivos(pesCod, prjCod).subscribe((objetivos) => {
        this.objetivosPorProjeto.set(prjCod, objetivos);
        this.cdr.detectChanges();
      });
    }
  }

  onObjetivoChange(idx: number, objCod: number | null) {
    const ind = this.form.indicadoresMetricasDTOList[idx];
    const prjCod = this.escopos[idx].prjCod;
    if (objCod != null && prjCod != null) {
      const obj = (this.objetivosPorProjeto.get(prjCod) ?? []).find(
        (o) => o.objCod === Number(objCod),
      );
      if (obj) ind.objetivosDTOResponse = { ...obj };
    }
  }

  projetosDoPlano(pesCod: number | null): ProjetoDTO[] {
    return pesCod != null ? this.projetosPorPlano.get(pesCod) ?? [] : [];
  }

  objetivosDoProjeto(prjCod: number | null): ObjetivoDTO[] {
    return prjCod != null ? this.objetivosPorProjeto.get(prjCod) ?? [] : [];
  }

  salvar() {
    const acao = this.isNovo
      ? this.service.save(this.form)
      : this.service.update(this.metCod, this.form);

    acao.subscribe({
      next: (data) => {
        this.mostrarToast(
          this.isNovo ? 'Quadro criado com sucesso!' : 'Quadro atualizado com sucesso!',
          'sucesso',
        );
        if (this.isNovo && data.metCod != null) {
          this.router.navigate(['/metricas', data.metCod], { replaceUrl: true });
        }
      },
      error: () => this.mostrarToast('Erro ao salvar o quadro.', 'erro'),
    });
  }

  deletar() {
    this.service.delete(this.metCod).subscribe(() => this.voltar());
  }

  voltar() {
    this.router.navigate(['/metricas']);
  }

  private resetarObjetivo(idx: number) {
    const ind = this.form.indicadoresMetricasDTOList[idx];
    ind.objetivosDTOResponse = {
      objCod: 0,
      nome: '',
      dataInicio: '',
      dataTermino: '',
      prjCod: 0,
    };
  }

  private popularEscoposParaIndicadores() {
    const indicadores = this.form.indicadoresMetricasDTOList;
    this.escopos = indicadores.map(() => ({ pesCod: null, prjCod: null }));

    if (indicadores.length === 0) return;

    this.planejamentoService.findAll().subscribe((planos) => {
      this.planos = planos;
      if (planos.length === 0) {
        this.cdr.detectChanges();
        return;
      }

      const requests = planos.map((p) =>
        this.planejamentoService
          .findProjetos(p.pesCod!)
          .pipe(map((projetos) => ({ pesCod: p.pesCod!, projetos }))),
      );

      forkJoin(requests).subscribe((resultados) => {
        resultados.forEach(({ pesCod, projetos }) => {
          this.projetosPorPlano.set(pesCod, projetos);
        });

        const projetosObsParaCarregar = new Set<number>();

        indicadores.forEach((ind, idx) => {
          const prjCod = ind.objetivosDTOResponse.prjCod!;
          const planoEncontrado = resultados.find((r) =>
            r.projetos.some((p) => p.prjCod === prjCod),
          );
          if (planoEncontrado) {
            this.escopos[idx] = { pesCod: planoEncontrado.pesCod, prjCod };
            if (!this.objetivosPorProjeto.has(prjCod)) {
              projetosObsParaCarregar.add(prjCod);
            }
          }
        });

        if (projetosObsParaCarregar.size === 0) {
          this.cdr.detectChanges();
          return;
        }

        const objRequests = Array.from(projetosObsParaCarregar).map((prjCod) => {
          const escopo = this.escopos.find((e) => e.prjCod === prjCod);
          const pesCod = escopo!.pesCod!;
          return this.planejamentoService
            .findObjetivos(pesCod, prjCod)
            .pipe(map((objetivos) => ({ prjCod, objetivos })));
        });

        forkJoin(objRequests).subscribe((resObjs) => {
          resObjs.forEach(({ prjCod, objetivos }) => {
            this.objetivosPorProjeto.set(prjCod, objetivos);
          });
          this.cdr.detectChanges();
        });
      });
    });
  }

  private formVazio(): MetricaDTO {
    return { metCod: null, descricao: '', indicadoresMetricasDTOList: [] };
  }

  private indicadorVazio(): IndicadorMetricaDTO {
    return {
      indCod: null,
      descricao: '',
      meta: 0,
      metCod: null,
      objetivosDTOResponse: { objCod: 0, nome: '', dataInicio: '', dataTermino: '', prjCod: 0 },
      quantidadeMensalMetricasDTOList: this.meses.map((m) => ({ mesCod: m, quantidade: 0 })),
    };
  }

  private normalizarIndicador(indicador: IndicadorMetricaDTO): IndicadorMetricaDTO {
    const lista = [...(indicador.quantidadeMensalMetricasDTOList ?? [])];
    for (const m of this.meses) {
      if (!lista.find((x) => x.mesCod === m)) {
        lista.push({ mesCod: m, quantidade: 0 });
      }
    }
    lista.sort((a, b) => a.mesCod - b.mesCod);
    return { ...indicador, quantidadeMensalMetricasDTOList: lista };
  }

  private mostrarToast(texto: string, tipo: 'sucesso' | 'erro') {
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.toast = { texto, tipo };
    this.cdr.detectChanges();
    this.toastTimer = setTimeout(() => {
      this.toast = null;
      this.cdr.detectChanges();
    }, 3000);
  }
}
