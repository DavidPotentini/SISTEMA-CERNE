import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ValidacaoHipoteseService } from '../../core/services/validacao-hipotese/validacao-hipotese.service';
import {
  HipoteseDTO,
  hipoteseVazia,
} from '../../models/validacao-hipotese/validacao-hipotese.model';
import { BLOCO_LEAN_LABEL, EBlocoLeanCanvas } from '../../enums/bloco-lean-canvas.enum';
import {
  DECISAO_HIPOTESE_LABEL,
  EDecisaoHipotese,
  EResultadoHipotese,
  EStatusHipotese,
  RESULTADO_HIPOTESE_LABEL,
  STATUS_HIPOTESE_LABEL,
} from '../../enums/hipotese.enum';

@Component({
  selector: 'app-hipotese-detalhe',
  templateUrl: 'hipotese-detalhe.component.html',
  styleUrls: ['hipotese-detalhe.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class HipoteseDetalheComponent implements OnInit, OnDestroy {
  incCod = 0;
  qvhCod = 0;
  hipCod: number | null = null;
  isNovo = true;
  form: HipoteseDTO = hipoteseVazia();
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  readonly blocosLean = Object.values(EBlocoLeanCanvas);
  readonly statusOpcoes = Object.values(EStatusHipotese);
  readonly resultadoOpcoes = Object.values(EResultadoHipotese);
  readonly decisaoOpcoes = Object.values(EDecisaoHipotese);

  private toastTimer: ReturnType<typeof setTimeout> | null = null;

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
      const cod = p.get('hipCod');
      this.isNovo = !cod || cod === 'novo';

      if (this.isNovo) {
        this.hipCod = null;
        this.form = hipoteseVazia(this.qvhCod);
        this.cdr.detectChanges();
      } else {
        this.hipCod = Number(cod);
        this.carregar();
      }
    });
  }

  ngOnDestroy() {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  private carregar() {
    this.service.findHipoteses(this.incCod, this.qvhCod).subscribe({
      next: (data) => {
        const h = (data ?? []).find((x) => x.hipCod === this.hipCod);
        if (h) {
          this.form = this.normalizar(h);
        } else {
          this.mostrarToast('Hipótese não encontrada.', 'erro');
        }
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Erro ao carregar a hipótese.', 'erro'),
    });
  }

  blocoLabel(b: EBlocoLeanCanvas | null): string {
    return b ? BLOCO_LEAN_LABEL[b] : '';
  }

  statusLabel(s: EStatusHipotese | null): string {
    return s ? STATUS_HIPOTESE_LABEL[s] : '';
  }

  resultadoLabel(r: EResultadoHipotese | null): string {
    return r ? RESULTADO_HIPOTESE_LABEL[r] : '';
  }

  decisaoLabel(d: EDecisaoHipotese | null): string {
    return d ? DECISAO_HIPOTESE_LABEL[d] : '';
  }

  salvar() {
    this.form.qvhCod = String(this.qvhCod);
    this.service.saveHipotese(this.incCod, this.qvhCod, this.hipCod, this.form).subscribe({
      next: (data) => {
        const normalizada = this.normalizar(data);
        if (this.isNovo && normalizada.hipCod != null) {
          this.hipCod = normalizada.hipCod;
          this.isNovo = false;
          this.form = normalizada;
          this.mostrarToast('Hipótese criada.', 'sucesso');
          this.router.navigate(
            ['/incubadas', this.incCod, 'validacaoHipotese', this.qvhCod, 'hipoteses', normalizada.hipCod],
            { replaceUrl: true },
          );
        } else {
          this.form = normalizada;
          this.mostrarToast('Hipótese atualizada.', 'sucesso');
          this.cdr.detectChanges();
        }
      },
      error: () => this.mostrarToast('Erro ao salvar a hipótese.', 'erro'),
    });
  }

  excluir() {
    if (this.hipCod == null) return;
    if (!confirm('Excluir esta hipótese?')) return;
    this.service.deleteHipotese(this.incCod, this.qvhCod, this.hipCod).subscribe({
      next: () => this.voltar(),
      error: () => this.mostrarToast('Erro ao excluir a hipótese.', 'erro'),
    });
  }

  voltar() {
    this.router.navigate(['/incubadas', this.incCod, 'validacaoHipotese', this.qvhCod]);
  }

  private normalizar(h: HipoteseDTO): HipoteseDTO {
    return {
      hipCod: h.hipCod ?? null,
      tituloHipotese: h.tituloHipotese ?? '',
      vldBlocoLeanCanvas: h.vldBlocoLeanCanvas ?? null,
      hipotese: h.hipotese ?? '',
      experimento: h.experimento ?? '',
      metrica: h.metrica ?? '',
      vldStatusHipotese: h.vldStatusHipotese ?? null,
      vldResultadoHipotese: h.vldResultadoHipotese ?? null,
      resultadoDetalhamento: h.resultadoDetalhamento ?? '',
      vldDecisaoHipotese: h.vldDecisaoHipotese ?? null,
      decisaoDetalhamento: h.decisaoDetalhamento ?? '',
      qvhCod: h.qvhCod ?? String(this.qvhCod),
    };
  }

  private mostrarToast(texto: string, tipo: 'sucesso' | 'erro') {
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.toast = { texto, tipo };
    this.cdr.detectChanges();
    this.toastTimer = setTimeout(() => {
      this.toast = null;
      this.cdr.detectChanges();
    }, 2500);
  }
}
