import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PlanejamentoService } from '../../../core/services/planejamento/planajamento.service';
import { PlanejamentoResponse } from '../../../models/planejamento/planejamento.model';

@Component({
  selector: 'app-planejamento',
  templateUrl: 'planejamento.component.html',
  styleUrls: ['planejamento.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class PlanejamentoComponent implements OnInit {
  planejamentos: PlanejamentoResponse[] = [];
  erroCarregamento: string | null = null;

  constructor(
    private service: PlanejamentoService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.service.findAll().subscribe({
      next: data => { this.planejamentos = data; this.cdr.detectChanges(); },
      error: err => { this.erroCarregamento = `Erro ao carregar planejamentos: ${err.message ?? err.status ?? 'desconhecido'}`; this.cdr.detectChanges(); },
    });
  }

  novo() {
    this.router.navigate(['/planejamento/novo']);
  }

  editar(pesCod: number) {
    this.router.navigate(['/planejamento', pesCod]);
  }

  abrirProjetos(pesCod: number) {
    this.router.navigate(['/', pesCod, 'projetos']);
  }

  deletar(pesCod: number) {
    this.service.delete(pesCod).subscribe(() => {
      this.planejamentos = this.planejamentos.filter(p => p.pesCod !== pesCod);
    });
  }
}
