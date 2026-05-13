import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PlanejamentoService } from '../../../core/services/planejamento/planajamento.service';
import { ProjetoResponse } from '../../../models/planejamento/projeto.model';

@Component({
  selector: 'app-projeto',
  templateUrl: 'projeto.component.html',
  styleUrls: ['projeto.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class ProjetoComponent implements OnInit {
  projetos: ProjetoResponse[] = [];
  planejamentoNome = '';
  pesCod = 0;
  erroCarregamento: string | null = null;

  constructor(
    private service: PlanejamentoService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.pesCod = Number(this.route.snapshot.paramMap.get('pesCod'));
    this.service.findProjetos(this.pesCod).subscribe({
      next: data => { this.projetos = data; this.cdr.detectChanges(); },
      error: err => { this.erroCarregamento = `Erro ao carregar projetos: ${err.message ?? err.status ?? 'desconhecido'}`; this.cdr.detectChanges(); },
    });
    this.service.findById(this.pesCod).subscribe(data => { this.planejamentoNome = data.nome; this.cdr.detectChanges(); });
  }

  novo() {
    this.router.navigate(['/', this.pesCod, 'projetos', 'novo']);
  }

  editar(prjCod: number) {
    this.router.navigate(['/', this.pesCod, 'projetos', prjCod]);
  }

  abrirObjetivos(prjCod: number) {
    this.router.navigate(['/', this.pesCod, 'projetos', prjCod, 'objetivos']);
  }

  deletar(prjCod: number) {
    this.service.deleteProjeto(this.pesCod, prjCod).subscribe(() => {
      this.projetos = this.projetos.filter(p => p.prjCod !== prjCod);
      this.cdr.detectChanges();
    });
  }

  voltar() {
    this.router.navigate(['/']);
  }
}
