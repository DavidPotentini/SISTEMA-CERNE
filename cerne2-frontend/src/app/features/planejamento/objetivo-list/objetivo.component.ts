import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PlanejamentoService } from '../../../core/services/planejamento/planajamento.service';
import { ObjetivoResponse } from '../../../models/planejamento/objetivo.model';

@Component({
  selector: 'app-objetivo',
  templateUrl: 'objetivo.component.html',
  styleUrls: ['objetivo.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class ObjetivoComponent implements OnInit {
  objetivos: ObjetivoResponse[] = [];
  projetoNome = '';
  pesCod = 0;
  prjCod = 0;
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
    this.service.findObjetivos(this.pesCod, this.prjCod).subscribe({
      next: data => { this.objetivos = data; this.cdr.detectChanges(); },
      error: err => { this.erroCarregamento = `Erro ao carregar objetivos: ${err.message ?? err.status ?? 'desconhecido'}`; this.cdr.detectChanges(); },
    });
    this.service.findByProjetoId(this.pesCod, this.prjCod).subscribe(data => { this.projetoNome = data.nome; this.cdr.detectChanges(); });
  }

  novo() {
    this.router.navigate(['/', this.pesCod, 'projetos', this.prjCod, 'objetivos', 'novo']);
  }

  editar(objCod: number) {
    this.router.navigate(['/', this.pesCod, 'projetos', this.prjCod, 'objetivos', objCod]);
  }

  abrirTarefas(objCod: number) {
    this.router.navigate(['/', this.pesCod, 'projetos', this.prjCod, 'objetivos', objCod, 'tarefas']);
  }

  deletar(objCod: number) {
    this.service.deleteObjetivo(this.pesCod, this.prjCod, objCod).subscribe(() => {
      this.objetivos = this.objetivos.filter(o => o.objCod !== objCod);
      this.cdr.detectChanges();
    });
  }

  voltar() {
    this.router.navigate(['/', this.pesCod, 'projetos']);
  }
}
