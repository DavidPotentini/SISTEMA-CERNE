import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PlanejamentoService } from '../../../core/services/planejamento/planajamento.service';
import { ProjetoDTO } from '../../../models/planejamento/projeto.model';

@Component({
  selector: 'app-projeto',
  templateUrl: 'projeto.component.html',
  styleUrls: ['projeto.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class ProjetoComponent implements OnInit {
  projetos: ProjetoDTO[] = [];
  planejamentoNome = '';
  pesCod = 0;
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

    this.service.findProjetos(this.pesCod, this.incCod).subscribe({
      next: data => { this.projetos = data; this.cdr.detectChanges(); },
      error: err => { this.erroCarregamento = `Erro ao carregar projetos: ${err.message ?? err.status ?? 'desconhecido'}`; this.cdr.detectChanges(); },
    });
    this.service.findById(this.pesCod, this.incCod).subscribe(data => { this.planejamentoNome = data.nome; this.cdr.detectChanges(); });
  }

  private prefixo(): unknown[] {
    if (this.modoAvaliacao && this.incCod != null)
      return ['/gerenciaIncubadas', '/incubadas/', this.incCod, 'planejamento'];
    if (this.incCod != null) return ['/incubadas/', this.incCod, 'planejamento'];
    return ['/planejamento'];
  }

  private caminhoProjetos(): unknown[] {
    return [...this.prefixo(), this.pesCod, 'projetos'];
  }

  novo() {
    this.router.navigate([...this.caminhoProjetos(), 'novo']);
  }

  editar(prjCod: number) {
    this.router.navigate([...this.caminhoProjetos(), prjCod]);
  }

  abrirObjetivos(prjCod: number) {
    this.router.navigate([...this.caminhoProjetos(), prjCod, 'objetivos']);
  }

  deletar(prjCod: number) {
    this.service.deleteProjeto(this.pesCod, prjCod, this.incCod).subscribe(() => {
      this.projetos = this.projetos.filter(p => p.prjCod !== prjCod);
      this.cdr.detectChanges();
    });
  }

  voltar() {
    this.router.navigate(this.prefixo());
  }
}
