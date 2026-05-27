import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ObjetivoDTO } from '../../../models/planejamento/objetivo.model';
import { PlanejamentoService } from '../../../core/services/planejamento/planajamento.service';

@Component({
  selector: 'app-objetivo-detalhe',
  templateUrl: 'objetivo-detalhe.component.html',
  styleUrls: ['objetivo-detalhe.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule],
})
export class ObjetivoDetalheComponent implements OnInit, OnDestroy {
  form: ObjetivoDTO = this.formVazio();
  pesCod = 0;
  prjCod = 0;
  objCod = 0;
  incCod: number | null = null;
  modoAvaliacao = false;
  isNovo = true;
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private service: PlanejamentoService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.modoAvaliacao = !!this.route.snapshot.data['modoAvaliacao'];
    this.route.paramMap.subscribe(params => {
      const inc = params.get('incCod');
      this.incCod = inc ? Number(inc) : null;
      this.pesCod = Number(params.get('pesCod'));
      this.prjCod = Number(params.get('prjCod'));
      const cod = params.get('objCod');
      this.isNovo = !cod || cod === 'novo';

      if (this.isNovo) {
        this.form = this.formVazio();
        this.objCod = 0;
      } else {
        this.objCod = Number(cod);
        this.service.findByObjetivoId(this.pesCod, this.prjCod, this.objCod, this.incCod).subscribe(data => {
          this.form = data;
          this.cdr.detectChanges();
        });
      }
    });
  }

  ngOnDestroy() {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  private prefixo(): unknown[] {
    if (this.modoAvaliacao && this.incCod != null)
      return ['/gerenciaIncubadas', '/incubadas/', this.incCod, 'planejamento'];
    if (this.incCod != null) return ['/incubadas/', this.incCod, 'planejamento'];
    return ['/planejamento'];
  }

  private caminhoObjetivos(): unknown[] {
    return [...this.prefixo(), this.pesCod, 'projetos', this.prjCod, 'objetivos'];
  }

  salvar() {
    if (this.modoAvaliacao) return;
    if (this.isNovo) {
      this.service.saveObjetivo(this.pesCod, this.prjCod, this.form, this.incCod).subscribe({
        next: data => {
          this.isNovo = false;
          this.objCod = data.objCod!;
          this.mostrarToast('Objetivo criado com sucesso!', 'sucesso');
          this.router.navigate(
            [...this.caminhoObjetivos(), data.objCod],
            { replaceUrl: true },
          );
        },
        error: () => this.mostrarToast('Erro ao criar o objetivo.', 'erro'),
      });
    } else {
      this.service.updateObjetivo(this.pesCod, this.prjCod, this.objCod, this.form, this.incCod).subscribe({
        next: () => this.mostrarToast('Objetivo atualizado com sucesso!', 'sucesso'),
        error: () => this.mostrarToast('Erro ao atualizar o objetivo.', 'erro'),
      });
    }
  }

  deletar() {
    if (this.modoAvaliacao) return;
    this.service.deleteObjetivo(this.pesCod, this.prjCod, this.objCod, this.incCod).subscribe(() => this.voltar());
  }

  voltar() {
    this.router.navigate(this.caminhoObjetivos());
  }

  private mostrarToast(texto: string, tipo: 'sucesso' | 'erro') {
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.toast = { texto, tipo };
    this.cdr.detectChanges();
    this.toastTimer = setTimeout(() => {
      this.toast = null;
      this.cdr.detectChanges();
    }, 3000);
  }

  private formVazio(): ObjetivoDTO {
    return { objCod: null, nome: '', dataInicio: '', dataTermino: '', prjCod: null };
  }
}
