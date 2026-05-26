import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PlanejamentoService } from '../../../core/services/planejamento/planajamento.service';
import { ObjetivoDTO } from '../../../models/planejamento/objetivo.model';

@Component({
  selector: 'app-objetivo',
  templateUrl: 'objetivo.component.html',
  styleUrls: ['objetivo.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class ObjetivoComponent implements OnInit {
  objetivos: ObjetivoDTO[] = [];
  projetoNome = '';
  pesCod = 0;
  prjCod = 0;
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

    this.service.findObjetivos(this.pesCod, this.prjCod).subscribe({
      next: data => { this.objetivos = data; this.cdr.detectChanges(); },
      error: err => { this.erroCarregamento = `Erro ao carregar objetivos: ${err.message ?? err.status ?? 'desconhecido'}`; this.cdr.detectChanges(); },
    });
    this.service.findByProjetoId(this.pesCod, this.prjCod).subscribe(data => { this.projetoNome = data.nome; this.cdr.detectChanges(); });
  }

  private prefixo(): unknown[] {
    if (this.modoAvaliacao && this.incCod != null)
      return ['/gerenciaIncubadas', this.incCod, 'planejamento'];
    if (this.incCod != null) return ['/', this.incCod, 'planejamento'];
    return ['/planejamento'];
  }

  private caminhoObjetivos(): unknown[] {
    return [...this.prefixo(), this.pesCod, 'projetos', this.prjCod, 'objetivos'];
  }

  private caminhoProjetos(): unknown[] {
    return [...this.prefixo(), this.pesCod, 'projetos'];
  }

  novo() {
    this.router.navigate([...this.caminhoObjetivos(), 'novo']);
  }

  editar(objCod: number) {
    this.router.navigate([...this.caminhoObjetivos(), objCod]);
  }

  abrirTarefas(objCod: number) {
    this.router.navigate([...this.caminhoObjetivos(), objCod, 'tarefas']);
  }

  deletar(objCod: number) {
    this.service.deleteObjetivo(this.pesCod, this.prjCod, objCod).subscribe(() => {
      this.objetivos = this.objetivos.filter(o => o.objCod !== objCod);
      this.cdr.detectChanges();
    });
  }

  voltar() {
    this.router.navigate(this.caminhoProjetos());
  }
}
