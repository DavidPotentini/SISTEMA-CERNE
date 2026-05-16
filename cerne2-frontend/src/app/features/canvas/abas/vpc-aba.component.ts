import { ChangeDetectorRef, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { CanvasService } from '../../../core/services/canvas/canvas.service';
import {
  ValuePropositionCanvasDTO,
  campoVazio,
  vpcVazio,
} from '../../../models/canvas/canvas.model';

@Component({
  selector: 'app-vpc-aba',
  templateUrl: 'vpc-aba.component.html',
  styleUrls: ['vpc-aba.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class VpcAbaComponent implements OnChanges {
  @Input() incCod = 0;
  @Input() ambcCod = 0;

  form: ValuePropositionCanvasDTO = vpcVazio();
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
    this.service.findVPC(this.incCod, this.ambcCod).subscribe({
      next: (data) => {
        this.form = this.normalizar(data);
        this.existente = true;
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 404) {
          this.form = vpcVazio(this.ambcCod);
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
      ? this.service.updateVPC(this.incCod, this.ambcCod, this.form)
      : this.service.saveVPC(this.incCod, this.ambcCod, this.form);

    acao.subscribe({
      next: (data) => {
        this.form = this.normalizar(data);
        this.existente = true;
        this.mostrarToast('Canvas Proposta de Valor salvo.', 'sucesso');
      },
      error: () => this.mostrarToast('Erro ao salvar.', 'erro'),
    });
  }

  private normalizar(d: ValuePropositionCanvasDTO): ValuePropositionCanvasDTO {
    return {
      vpcCod: d.vpcCod ?? null,
      ambcCod: d.ambcCod ?? this.ambcCod,
      criadoresGanho: d.criadoresGanho ?? campoVazio(),
      produtosServicos: d.produtosServicos ?? campoVazio(),
      alivioDores: d.alivioDores ?? campoVazio(),
      ganhos: d.ganhos ?? campoVazio(),
      dores: d.dores ?? campoVazio(),
      tarefasClientes: d.tarefasClientes ?? campoVazio(),
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
