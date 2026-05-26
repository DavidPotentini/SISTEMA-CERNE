import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TarefaDTO } from '../../../models/planejamento/tarefa.model';
import { PlanejamentoService } from '../../../core/services/planejamento/planajamento.service';
import { ESituacaoTarefa } from '../../../enums/situacao-tarefa.enum';
import { PessoasService } from '../../../core/services/pessoas/pessoas.service';
import { PessoaDTO } from '../../../models/pessoas/pessoa.model';
import { EvidenciasComponent } from './evidencias/evidencias.component';

@Component({
  selector: 'app-tarefa-detalhe',
  templateUrl: 'tarefa-detalhe.component.html',
  styleUrls: ['tarefa-detalhe.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule, EvidenciasComponent],
})
export class TarefaDetalheComponent implements OnInit, OnDestroy {
  form: TarefaDTO = this.formVazio();
  pesCod = 0;
  prjCod = 0;
  objCod = 0;
  trfCod = 0;
  incCod: number | null = null;
  modoAvaliacao = false;
  isNovo = true;
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  readonly situacoes = Object.values(ESituacaoTarefa);
  responsaveis: PessoaDTO[] = [];

  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private service: PlanejamentoService,
    private pessoasService: PessoasService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.modoAvaliacao = !!this.route.snapshot.data['modoAvaliacao'];

    this.pessoasService.findByIncubadora().subscribe(data => {
      this.responsaveis = data ?? [];
      this.cdr.detectChanges();
    });

    this.route.paramMap.subscribe(params => {
      const inc = params.get('incCod');
      this.incCod = inc ? Number(inc) : null;
      this.pesCod = Number(params.get('pesCod'));
      this.prjCod = Number(params.get('prjCod'));
      this.objCod = Number(params.get('objCod'));
      const cod = params.get('trfCod');
      this.isNovo = !cod || cod === 'novo';

      if (this.isNovo) {
        this.form = this.formVazio();
        this.trfCod = 0;
      } else {
        this.trfCod = Number(cod);
        this.service.findByTarefaId(this.pesCod, this.prjCod, this.objCod, this.trfCod).subscribe(data => {
          this.form = data;
          this.cdr.detectChanges();
        });
      }
    });
  }

  ngOnDestroy() {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  private prefixo(): unknown[] {
    if (this.modoAvaliacao && this.incCod != null)
      return ['/gerenciaIncubadas', this.incCod, 'planejamento'];
    if (this.incCod != null) return ['/', this.incCod, 'planejamento'];
    return ['/planejamento'];
  }

  private caminhoTarefas(): unknown[] {
    return [...this.prefixo(), this.pesCod, 'projetos', this.prjCod, 'objetivos', this.objCod, 'tarefas'];
  }

  salvar() {
    if (this.modoAvaliacao) {
      this.salvarAvaliacao();
      return;
    }

    if (this.isNovo) {
      this.service.saveTarefa(this.pesCod, this.prjCod, this.objCod, this.form).subscribe({
        next: data => {
          this.isNovo = false;
          this.trfCod = data.trfCod!;
          this.mostrarToast('Tarefa criada com sucesso!', 'sucesso');
          this.router.navigate(
            [...this.caminhoTarefas(), data.trfCod],
            { replaceUrl: true },
          );
        },
        error: () => this.mostrarToast('Erro ao criar a tarefa.', 'erro'),
      });
    } else {
      this.service.updateTarefa(this.pesCod, this.prjCod, this.objCod, this.trfCod, this.form).subscribe({
        next: () => this.mostrarToast('Tarefa atualizada com sucesso!', 'sucesso'),
        error: () => this.mostrarToast('Erro ao atualizar a tarefa.', 'erro'),
      });
    }
  }

  private salvarAvaliacao() {
    if (this.incCod == null || this.isNovo) return;
    this.service
      .avaliarTarefa(this.incCod, this.pesCod, this.prjCod, this.objCod, this.trfCod, {
        pontuacao: this.form.pontuacao,
        observacao: this.form.observacao,
      })
      .subscribe({
        next: () => this.mostrarToast('Avaliação salva.', 'sucesso'),
        error: () => this.mostrarToast('Erro ao salvar a avaliação.', 'erro'),
      });
  }

  deletar() {
    if (this.modoAvaliacao) return;
    this.service
      .deleteTarefa(this.pesCod, this.prjCod, this.objCod, this.trfCod)
      .subscribe(() => this.voltar());
  }

  voltar() {
    this.router.navigate(this.caminhoTarefas());
  }

  situacaoLabel(s: string): string {
    const map: Record<string, string> = {
      PENDENTE: 'Pendente',
      EM_ANDAMENTO: 'Em andamento',
      FINALIZADA: 'Finalizada',
    };
    return map[s] ?? s;
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

  private formVazio(): TarefaDTO {
    return {
      trfCod: null,
      nome: '',
      dataInicio: '',
      dataTermino: '',
      eSituacaoTarefa: ESituacaoTarefa.PENDENTE,
      objCod: null,
      respCod: null,
      pontuacao: null,
      observacao: null,
    };
  }
}
