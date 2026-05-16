import { ChangeDetectorRef, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { CanvasService } from '../../../core/services/canvas/canvas.service';
import { LeanCanvasDTO, campoVazio, leanVazio } from '../../../models/canvas/canvas.model';

@Component({
  selector: 'app-lean-aba',
  templateUrl: 'lean-aba.component.html',
  styleUrls: ['lean-aba.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class LeanAbaComponent implements OnChanges {
  @Input() incCod = 0;
  @Input() ambcCod = 0;

  form: LeanCanvasDTO = leanVazio();
  existente = false;
  carregando = false;
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  constructor(
    private service: CanvasService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnChanges(c: SimpleChanges) {
    if ((c['ambcCod'] || c['incCod']) && this.ambcCod && this.incCod) this.carregar();
  }

  carregar() {
    this.carregando = true;
    this.service.findLean(this.incCod, this.ambcCod).subscribe({
      next: (data) => {
        this.form = this.normalizar(data);
        this.existente = true;
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 404) {
          this.form = leanVazio(this.ambcCod);
          this.existente = false;
        }
        this.carregando = false;
        this.cdr.detectChanges();
      },
    });
  }

  salvar() {
    this.form.ambcCod = this.ambcCod;
    const acao = this.existente
      ? this.service.updateLean(this.incCod, this.ambcCod, this.form)
      : this.service.saveLean(this.incCod, this.ambcCod, this.form);

    acao.subscribe({
      next: (data) => {
        this.form = this.normalizar(data);
        this.existente = true;
        this.mostrarToast('Lean Canvas salvo.', 'sucesso');
      },
      error: () => this.mostrarToast('Erro ao salvar o Lean Canvas.', 'erro'),
    });
  }

  private normalizar(d: LeanCanvasDTO): LeanCanvasDTO {
    return {
      leanCod: d.leanCod ?? null,
      ambcCod: d.ambcCod ?? this.ambcCod,
      problema: d.problema ?? campoVazio(),
      solucao: d.solucao ?? campoVazio(),
      metrica: d.metrica ?? campoVazio(),
      propostaValor: d.propostaValor ?? campoVazio(),
      competenciaEssencial: d.competenciaEssencial ?? campoVazio(),
      canais: d.canais ?? campoVazio(),
      segmentoCliente: d.segmentoCliente ?? campoVazio(),
      estruturaCustos: d.estruturaCustos ?? campoVazio(),
      modeloReceita: d.modeloReceita ?? campoVazio(),
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
