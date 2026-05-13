import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TarefaRequest } from '../../../models/planejamento/tarefa.model';
import { PlanejamentoService } from '../../../core/services/planejamento/planajamento.service';
import { ESituacaoTarefa } from '../../../enums/situacao-tarefa.enum';
import { PessoasService } from '../../../core/services/pessoas/pessoas.service';
import { PessoaResponse } from '../../../models/pessoas/pessoa.model';
import { EvidenciasComponent } from './evidencias/evidencias.component';

@Component({
  selector: 'app-tarefa-detalhe',
  templateUrl: 'tarefa-detalhe.component.html',
  styleUrls: ['tarefa-detalhe.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule, EvidenciasComponent],
})
export class TarefaDetalheComponent implements OnInit, OnDestroy {
  form: TarefaRequest = this.formVazio();
  pesCod = 0;
  prjCod = 0;
  objCod = 0;
  trfCod = 0;
  isNovo = true;
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  readonly situacoes = Object.values(ESituacaoTarefa);
  responsaveis: PessoaResponse[] = [];

  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private service: PlanejamentoService,
    private pessoasService: PessoasService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.pessoasService.findByIncubadora().subscribe(data => {
      this.responsaveis = data ?? [];
      this.cdr.detectChanges();
    });

    this.route.paramMap.subscribe(params => {
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
          const { trfCod, objCod, ...request } = data;
          this.form = request;
          this.cdr.detectChanges();
        });
      }
    });
  }

  ngOnDestroy() {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  salvar() {
    if (this.isNovo) {
      this.service.saveTarefa(this.pesCod, this.prjCod, this.objCod, this.form).subscribe({
        next: data => {
          this.isNovo = false;
          this.trfCod = data.trfCod;
          this.mostrarToast('Tarefa criada com sucesso!', 'sucesso');
          this.router.navigate(
            ['/', this.pesCod, 'projetos', this.prjCod, 'objetivos', this.objCod, 'tarefas', data.trfCod],
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

  deletar() {
    this.service
      .deleteTarefa(this.pesCod, this.prjCod, this.objCod, this.trfCod)
      .subscribe(() => this.voltar());
  }

  voltar() {
    this.router.navigate(['/', this.pesCod, 'projetos', this.prjCod, 'objetivos', this.objCod, 'tarefas']);
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

  private formVazio(): TarefaRequest {
    return {
      nome: '',
      dataInicio: '',
      dataTermino: '',
      eSituacaoTarefa: ESituacaoTarefa.PENDENTE,
      respCod: 0,
    };
  }
}
