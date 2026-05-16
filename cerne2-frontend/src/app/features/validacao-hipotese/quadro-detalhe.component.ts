import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ValidacaoHipoteseService } from '../../core/services/validacao-hipotese/validacao-hipotese.service';
import {
  HipoteseDTO,
  QuadroValidacaoHipoteseDTO,
  hipoteseVazia,
  quadroVazio,
} from '../../models/validacao-hipotese/validacao-hipotese.model';
import {
  BLOCO_LEAN_LABEL,
  EBlocoLeanCanvas,
} from '../../enums/bloco-lean-canvas.enum';
import {
  DECISAO_HIPOTESE_LABEL,
  EDecisaoHipotese,
  EResultadoHipotese,
  EStatusHipotese,
  RESULTADO_HIPOTESE_LABEL,
  STATUS_HIPOTESE_LABEL,
} from '../../enums/hipotese.enum';

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
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  readonly blocosLean = Object.values(EBlocoLeanCanvas);
  readonly statusOpcoes = Object.values(EStatusHipotese);
  readonly resultadoOpcoes = Object.values(EResultadoHipotese);
  readonly decisaoOpcoes = Object.values(EDecisaoHipotese);

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
          qvhCod: data.qvhCod,
          tituloQuadro: data.tituloQuadro ?? '',
          incCod: data.incCod ?? this.incCod,
          hipoteseDTOList: (data.hipoteseDTOList ?? []).map((h) => this.normalizarHipotese(h)),
        };
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Não foi possível carregar o quadro.', 'erro'),
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

  adicionarHipotese() {
    const nova = hipoteseVazia(this.qvhCod);
    nova.tituloHipotese = this.quadro.tituloQuadro;
    this.quadro.hipoteseDTOList = [...this.quadro.hipoteseDTOList, nova];
    this.cdr.detectChanges();
  }

  removerHipotese(h: HipoteseDTO) {
    if (h.hipCod == null) {
      this.quadro.hipoteseDTOList = this.quadro.hipoteseDTOList.filter((x) => x !== h);
      this.cdr.detectChanges();
      return;
    }
    if (!confirm('Excluir esta hipótese?')) return;
    this.service.deleteHipotese(this.incCod, this.qvhCod, h.hipCod).subscribe({
      next: () => {
        this.quadro.hipoteseDTOList = this.quadro.hipoteseDTOList.filter((x) => x !== h);
        this.mostrarToast('Hipótese excluída.', 'sucesso');
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Erro ao excluir hipótese.', 'erro'),
    });
  }

  salvar() {
    for (const h of this.quadro.hipoteseDTOList) {
      h.tituloHipotese = this.quadro.tituloQuadro;
      h.qvhCod = this.qvhCod == null ? null : String(this.qvhCod);
    }
    this.service.update(this.incCod, this.qvhCod, this.quadro).subscribe({
      next: (data) => {
        this.quadro = {
          ...data,
          hipoteseDTOList: (data.hipoteseDTOList ?? []).map((h) => this.normalizarHipotese(h)),
        };
        this.mostrarToast('Quadro salvo.', 'sucesso');
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Erro ao salvar o quadro.', 'erro'),
    });
  }

  voltar() {
    this.router.navigate(['/incubadas', this.incCod, 'quadrosValidacao']);
  }

  private normalizarHipotese(h: HipoteseDTO): HipoteseDTO {
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
      qvhCod: h.qvhCod ?? (this.qvhCod == null ? null : String(this.qvhCod)),
    };
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
