import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ValidacaoHipoteseService } from '../../core/services/validacao-hipotese/validacao-hipotese.service';
import { QuadroValidacaoHipoteseDTO } from '../../models/validacao-hipotese/validacao-hipotese.model';

@Component({
  selector: 'app-quadro-list',
  templateUrl: 'quadro-list.component.html',
  styleUrls: ['quadro-list.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class QuadroListComponent implements OnInit {
  incCod = 0;
  quadros: QuadroValidacaoHipoteseDTO[] = [];
  novoTitulo = '';
  carregando = true;

  constructor(
    private service: ValidacaoHipoteseService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe((p) => {
      this.incCod = Number(p.get('incCod'));
      this.carregar();
    });
  }

  carregar() {
    this.carregando = true;
    this.service.findAllByIncCod(this.incCod).subscribe({
      next: (data) => {
        this.quadros = data ?? [];
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.quadros = [];
        this.carregando = false;
        this.cdr.detectChanges();
      },
    });
  }

  criar() {
    const tituloQuadro = this.novoTitulo.trim();
    if (!tituloQuadro) return;
    this.service
      .save(this.incCod, {
        qvhCod: null,
        tituloQuadro,
        incCod: this.incCod,
      })
      .subscribe((q) => {
        this.novoTitulo = '';
        if (q.qvhCod != null) {
          this.router.navigate(['/incubadas', this.incCod, 'validacaoHipotese', q.qvhCod]);
        } else {
          this.carregar();
        }
      });
  }

  abrir(qvhCod: number | null) {
    if (qvhCod != null)
      this.router.navigate(['/incubadas', this.incCod, 'validacaoHipotese', qvhCod]);
  }

  voltar() {
    this.router.navigate(['/incubadas', this.incCod]);
  }
}
