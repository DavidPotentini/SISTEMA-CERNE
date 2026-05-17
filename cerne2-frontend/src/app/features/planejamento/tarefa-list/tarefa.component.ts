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
  erroCarregamento: string | null = null;

  constructor(
    private service: PlanejamentoService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.pesCod = Number(this.route.snapshot.paramMap.get('pesCod'));
    this.prjCod = Number(this.route.snapshot.paramMap.get('prjCod'));
    this.objCod = Number(this.route.snapshot.paramMap.get('objCod'));
    this.service.findTarefas(this.pesCod, this.prjCod, this.objCod).subscribe({
      next: data => { this.tarefas = data; this.cdr.detectChanges(); },
      error: err => { this.erroCarregamento = `Erro ao carregar tarefas: ${err.message ?? err.status ?? 'desconhecido'}`; this.cdr.detectChanges(); },
    });
    this.service
      .findByObjetivoId(this.pesCod, this.prjCod, this.objCod)
      .subscribe(data => { this.objetivoNome = data.nome; this.cdr.detectChanges(); });
  }

  novo() {
    this.router.navigate([
      '/',
      this.pesCod,
      'projetos',
      this.prjCod,
      'objetivos',
      this.objCod,
      'tarefas',
      'novo',
    ]);
  }

  editar(trfCod: number) {
    this.router.navigate([
      '/',
      this.pesCod,
      'projetos',
      this.prjCod,
      'objetivos',
      this.objCod,
      'tarefas',
      trfCod,
    ]);
  }

  deletar(trfCod: number) {
    this.service.deleteTarefa(this.pesCod, this.prjCod, this.objCod, trfCod).subscribe(() => {
      this.tarefas = this.tarefas.filter(t => t.trfCod !== trfCod);
      this.cdr.detectChanges();
    });
  }

  voltar() {
    this.router.navigate(['/', this.pesCod, 'projetos', this.prjCod, 'objetivos']);
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
