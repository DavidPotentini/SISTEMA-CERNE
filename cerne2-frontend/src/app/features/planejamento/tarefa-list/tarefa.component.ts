import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PlanejamentoService } from '../../../core/services/planejamento/planajamento.service';
import { TarefaDTO } from '../../../models/planejamento/tarefa.model';
import { ESituacaoTarefa } from '../../../enums/situacao-tarefa.enum';

@Component({
  selector: 'app-tarefa',
  templateUrl: 'tarefa.component.html',
  styleUrls: ['tarefa.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class TarefaComponent implements OnInit {
  tarefas: TarefaDTO[] = [];
  objetivoNome = '';
  pesCod = 0;
  prjCod = 0;
  objCod = 0;
  incCod: number | null = null;
  modoAvaliacao = false;
  erroCarregamento: string | null = null;

  constructor(
    private service: PlanejamentoService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.modoAvaliacao = !!this.route.snapshot.data['modoAvaliacao'];
    const params = this.route.snapshot.paramMap;
    const inc = params.get('incCod');
    this.incCod = inc ? Number(inc) : null;
    this.pesCod = Number(params.get('pesCod'));
    this.prjCod = Number(params.get('prjCod'));
    this.objCod = Number(params.get('objCod'));

    this.service.findTarefas(this.pesCod, this.prjCod, this.objCod).subscribe({
      next: data => { this.tarefas = data; this.cdr.detectChanges(); },
      error: err => { this.erroCarregamento = `Erro ao carregar tarefas: ${err.message ?? err.status ?? 'desconhecido'}`; this.cdr.detectChanges(); },
    });
    this.service
      .findByObjetivoId(this.pesCod, this.prjCod, this.objCod)
      .subscribe(data => { this.objetivoNome = data.nome; this.cdr.detectChanges(); });
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

  private caminhoObjetivos(): unknown[] {
    return [...this.prefixo(), this.pesCod, 'projetos', this.prjCod, 'objetivos'];
  }

  novo() {
    this.router.navigate([...this.caminhoTarefas(), 'novo']);
  }

  editar(trfCod: number) {
    this.router.navigate([...this.caminhoTarefas(), trfCod]);
  }

  deletar(trfCod: number) {
    this.service.deleteTarefa(this.pesCod, this.prjCod, this.objCod, trfCod).subscribe(() => {
      this.tarefas = this.tarefas.filter(t => t.trfCod !== trfCod);
      this.cdr.detectChanges();
    });
  }

  voltar() {
    this.router.navigate(this.caminhoObjetivos());
  }

  situacaoLabel(s: ESituacaoTarefa): string {
    const map: Record<string, string> = {
      PENDENTE: 'Pendente',
      EM_ANDAMENTO: 'Em andamento',
      FINALIZADA: 'Finalizada',
    };
    return map[s] ?? s;
  }
}
