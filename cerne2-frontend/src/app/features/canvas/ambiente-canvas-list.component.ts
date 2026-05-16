import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CanvasService } from '../../core/services/canvas/canvas.service';
import { AmbienteCanvasDTO } from '../../models/canvas/canvas.model';

@Component({
  selector: 'app-ambiente-canvas-list',
  templateUrl: 'ambiente-canvas-list.component.html',
  styleUrls: ['ambiente-canvas-list.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class AmbienteCanvasListComponent implements OnInit {
  incCod = 0;
  ambientes: AmbienteCanvasDTO[] = [];
  novoNome = '';
  carregando = true;

  constructor(
    private service: CanvasService,
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
    this.service.findAmbientesByIncCod(this.incCod).subscribe({
      next: (data) => {
        this.ambientes = data ?? [];
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.ambientes = [];
        this.carregando = false;
        this.cdr.detectChanges();
      },
    });
  }

  criar() {
    const descricao = this.novoNome.trim();
    if (!descricao) return;
    this.service
      .saveAmbiente(this.incCod, { ambcCod: null, descricao, incCod: this.incCod })
      .subscribe((amb) => {
        this.novoNome = '';
        if (amb.ambcCod != null) {
          this.router.navigate(['/incubadas', this.incCod, 'ambientesCanvas', amb.ambcCod]);
        } else {
          this.carregar();
        }
      });
  }

  abrir(ambcCod: number | null) {
    if (ambcCod != null)
      this.router.navigate(['/incubadas', this.incCod, 'ambientesCanvas', ambcCod]);
  }

  excluir(amb: AmbienteCanvasDTO, ev: Event) {
    ev.stopPropagation();
    if (amb.ambcCod == null) return;
    if (!confirm(`Excluir o ambiente "${amb.descricao}"?`)) return;
    this.service.deleteAmbiente(this.incCod, amb.ambcCod).subscribe(() => this.carregar());
  }

  voltar() {
    this.router.navigate(['/incubadas', this.incCod]);
  }
}
