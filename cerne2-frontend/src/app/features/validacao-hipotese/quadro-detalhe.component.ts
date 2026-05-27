import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ValidacaoHipoteseService } from '../../core/services/validacao-hipotese/validacao-hipotese.service';
import {
  HipoteseDTO,
  QuadroValidacaoHipoteseDTO,
  quadroVazio,
} from '../../models/validacao-hipotese/validacao-hipotese.model';
import { BLOCO_LEAN_LABEL, EBlocoLeanCanvas } from '../../enums/bloco-lean-canvas.enum';
import { EStatusHipotese, STATUS_HIPOTESE_LABEL } from '../../enums/hipotese.enum';

@Component({
  selector: 'app-quadro-detalhe',
  templateUrl: 'quadro-detalhe.component.html',
  styleUrls: ['quadro-detalhe.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class QuadroDetalheComponent implements OnInit {
  incCod = 0;
  qvhCod = 0;
  quadro: QuadroValidacaoHipoteseDTO = quadroVazio(0);
  hipoteses: HipoteseDTO[] = [];
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  constructor(
    private service: ValidacaoHipoteseService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe((p) => {
      this.incCod = Number(p.get('incCod'));
      this.qvhCod = Number(p.get('qvhCod'));
      this.carregar();
    });
  }

  carregar() {
    this.service.findById(this.incCod, this.qvhCod).subscribe({
      next: (data) => {
        this.quadro = {
          qvhCod: this.qvhCod,
          tituloQuadro: data.tituloQuadro ?? '',
          incCod: this.incCod,
        };
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Não foi possível carregar o quadro.', 'erro'),
    });
    this.service.findHipoteses(this.incCod, this.qvhCod).subscribe({
      next: (data) => {
        this.hipoteses = data ?? [];
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Não foi possível carregar as hipóteses.', 'erro'),
    });
  }

  blocoLabel(b: EBlocoLeanCanvas | null): string {
    return b ? BLOCO_LEAN_LABEL[b] : '';
  }

  statusLabel(s: EStatusHipotese | null): string {
    return s ? STATUS_HIPOTESE_LABEL[s] : '';
  }

  adicionarHipotese() {
    this.router.navigate([
      '/incubadas', this.incCod, 'validacaoHipotese', this.qvhCod, 'hipoteses', 'novo',
    ]);
  }

  abrirHipotese(hipCod: number | null) {
    if (hipCod == null) return;
    this.router.navigate([
      '/incubadas', this.incCod, 'validacaoHipotese', this.qvhCod, 'hipoteses', hipCod,
    ]);
  }

  salvarQuadro() {
    this.service.update(this.incCod, this.qvhCod, this.quadro).subscribe({
      next: (data) => {
        this.quadro = {
          qvhCod: this.qvhCod,
          tituloQuadro: data.tituloQuadro ?? '',
          incCod: this.incCod,
        };
        this.mostrarToast('Quadro salvo.', 'sucesso');
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Erro ao salvar o quadro.', 'erro'),
    });
  }

  excluirQuadro() {
    if (!confirm('Excluir este quadro? Todas as hipóteses serão removidas.')) return;
    this.service.delete(this.incCod, this.qvhCod).subscribe({
      next: () => this.voltar(),
      error: () => this.mostrarToast('Erro ao excluir o quadro.', 'erro'),
    });
  }

  voltar() {
    this.router.navigate(['/incubadas', this.incCod, 'validacaoHipotese']);
  }

  private mostrarToast(texto: string, tipo: 'sucesso' | 'erro') {
    this.toast = { texto, tipo };
    this.cdr.detectChanges();
    setTimeout(() => {
      this.toast = null;
      this.cdr.detectChanges();
    }, 2500);
  }
}
