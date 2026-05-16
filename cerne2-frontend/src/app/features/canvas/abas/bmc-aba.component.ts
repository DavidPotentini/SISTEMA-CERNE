import { ChangeDetectorRef, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { CanvasService } from '../../../core/services/canvas/canvas.service';
import {
  BusinessModelCanvasDTO,
  bmcVazio,
  campoVazio,
} from '../../../models/canvas/canvas.model';

@Component({
  selector: 'app-bmc-aba',
  templateUrl: 'bmc-aba.component.html',
  styleUrls: ['bmc-aba.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class BmcAbaComponent implements OnChanges {
  @Input() incCod = 0;
  @Input() ambcCod = 0;

  form: BusinessModelCanvasDTO = bmcVazio();
  existente = false;
  carregando = false;
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  constructor(
    private service: CanvasService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnChanges(c: SimpleChanges) {
    if ((c['ambcCod'] || c['incCod']) && this.ambcCod && this.incCod) {
      this.carregar();
    }
  }

  carregar() {
    this.carregando = true;
    this.service.findBMC(this.incCod, this.ambcCod).subscribe({
      next: (data) => {
        this.form = this.normalizar(data);
        this.existente = true;
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 404) {
          this.form = bmcVazio(this.ambcCod);
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
      ? this.service.updateBMC(this.incCod, this.ambcCod, this.form)
      : this.service.saveBMC(this.incCod, this.ambcCod, this.form);

    acao.subscribe({
      next: (data) => {
        this.form = this.normalizar(data);
        this.existente = true;
        this.mostrarToast('Business Model Canvas salvo.', 'sucesso');
      },
      error: () => this.mostrarToast('Erro ao salvar o BMC.', 'erro'),
    });
  }

  private normalizar(d: BusinessModelCanvasDTO): BusinessModelCanvasDTO {
    return {
      bmcCod: d.bmcCod ?? null,
      ambcCod: d.ambcCod ?? this.ambcCod,
      parceirosChave: d.parceirosChave ?? campoVazio(),
      atividadesChave: d.atividadesChave ?? campoVazio(),
      propostasValor: d.propostasValor ?? campoVazio(),
      relacionamentoCliente: d.relacionamentoCliente ?? campoVazio(),
      segmentoCliente: d.segmentoCliente ?? campoVazio(),
      canais: d.canais ?? campoVazio(),
      recursosChave: d.recursosChave ?? campoVazio(),
      estruturaCustos: d.estruturaCustos ?? campoVazio(),
      fontesReceitas: d.fontesReceitas ?? campoVazio(),
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
