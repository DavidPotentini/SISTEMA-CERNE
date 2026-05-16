import { ChangeDetectorRef, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CanvasService } from '../../../core/services/canvas/canvas.service';
import {
  ChannelImplementationCanvasDTO,
  campoVazio,
  channelImplVazio,
} from '../../../models/canvas/canvas.model';

@Component({
  selector: 'app-channel-aba',
  templateUrl: 'channel-aba.component.html',
  styleUrls: ['channel-aba.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class ChannelAbaComponent implements OnChanges {
  @Input() incCod = 0;
  @Input() ambcCod = 0;

  segmentos: ChannelImplementationCanvasDTO[] = [];
  segSelecionado: ChannelImplementationCanvasDTO | null = null;
  carregando = false;
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  readonly fases = [
    { chave: 'Conhecimento', ativ: 'atividadeConhecimento', rec: 'recursosConhecimento', par: 'parceirosConhecimento' },
    { chave: 'Avaliação', ativ: 'atividadeAvaliacao', rec: 'recursosAvaliacao', par: 'parceirosAvaliacao' },
    { chave: 'Compra', ativ: 'atividadeCompra', rec: 'recursosCompra', par: 'parceirosCompra' },
    { chave: 'Entrega', ativ: 'atividadeEntrega', rec: 'recursosEntrega', par: 'parceirosEntrega' },
    { chave: 'Pós-Venda', ativ: 'atividadePosVenda', rec: 'recursosPosVenda', par: 'parceirosPosVenda' },
  ] as const;

  constructor(
    private service: CanvasService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnChanges(c: SimpleChanges) {
    if ((c['ambcCod'] || c['incCod']) && this.ambcCod && this.incCod) this.carregar();
  }

  carregar() {
    this.carregando = true;
    this.service.findChannels(this.incCod, this.ambcCod).subscribe({
      next: (data) => {
        this.segmentos = (data ?? []).map((s) => this.normalizar(s));
        this.segSelecionado = this.segmentos[0] ?? null;
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.segmentos = [];
        this.segSelecionado = null;
        this.carregando = false;
        this.cdr.detectChanges();
      },
    });
  }

  adicionar() {
    const novo = channelImplVazio(this.ambcCod);
    this.segmentos = [...this.segmentos, novo];
    this.segSelecionado = novo;
    this.cdr.detectChanges();
  }

  selecionar(s: ChannelImplementationCanvasDTO) {
    this.segSelecionado = s;
  }

  campo(s: ChannelImplementationCanvasDTO, chave: string): { texto: string } {
    const ref = (s as any)[chave];
    if (!ref || typeof ref !== 'object') {
      (s as any)[chave] = campoVazio();
    }
    return (s as any)[chave];
  }

  rotuloSegmento(s: ChannelImplementationCanvasDTO, idx: number): string {
    if (s.nomeSegmento?.trim()) return s.nomeSegmento.trim();
    if (s.segCod != null) return `Segmento #${s.segCod}`;
    return `Novo segmento ${idx + 1}`;
  }

  salvar() {
    if (!this.segSelecionado) return;
    const s = this.segSelecionado;
    s.ambcCod = this.ambcCod;

    const acao =
      s.segCod == null
        ? this.service.saveChannel(this.incCod, this.ambcCod, s)
        : this.service.updateChannel(this.incCod, this.ambcCod, s.segCod, s);

    acao.subscribe({
      next: (data) => {
        const idx = this.segmentos.indexOf(s);
        const normalizado = this.normalizar(data);
        if (idx >= 0) this.segmentos[idx] = normalizado;
        this.segSelecionado = normalizado;
        this.mostrarToast('Segmento salvo.', 'sucesso');
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Erro ao salvar.', 'erro'),
    });
  }

  excluir() {
    if (!this.segSelecionado) return;
    const s = this.segSelecionado;
    if (s.segCod == null) {
      this.segmentos = this.segmentos.filter((x) => x !== s);
      this.segSelecionado = this.segmentos[0] ?? null;
      this.cdr.detectChanges();
      return;
    }
    if (!confirm(`Excluir o segmento #${s.segCod}?`)) return;
    this.service.deleteChannel(this.incCod, this.ambcCod, s.segCod).subscribe(() => {
      this.segmentos = this.segmentos.filter((x) => x !== s);
      this.segSelecionado = this.segmentos[0] ?? null;
      this.cdr.detectChanges();
    });
  }

  private normalizar(s: ChannelImplementationCanvasDTO): ChannelImplementationCanvasDTO {
    const base = channelImplVazio(this.ambcCod);
    const result: ChannelImplementationCanvasDTO = { ...base, ...s, ambcCod: s.ambcCod ?? this.ambcCod };
    for (const k of Object.keys(base) as (keyof ChannelImplementationCanvasDTO)[]) {
      if (k === 'ambcCod' || k === 'segCod' || k === 'nomeSegmento') continue;
      const v = (result as any)[k];
      if (!v || typeof v !== 'object') (result as any)[k] = campoVazio();
    }
    return result;
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
