import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProjetoRequest } from '../../../models/planejamento/projeto.model';
import { PlanejamentoService } from '../../../core/services/planejamento/planajamento.service';

@Component({
  selector: 'app-projeto-detalhe',
  templateUrl: 'projeto-detalhe.component.html',
  styleUrls: ['projeto-detalhe.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule],
})
export class ProjetoDetalheComponent implements OnInit, OnDestroy {
  form: ProjetoRequest = this.formVazio();
  pesCod = 0;
  prjCod = 0;
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
    this.route.paramMap.subscribe(params => {
      this.pesCod = Number(params.get('pesCod'));
      const cod = params.get('prjCod');
      this.isNovo = !cod || cod === 'novo';

      if (this.isNovo) {
        this.form = this.formVazio();
        this.prjCod = 0;
      } else {
        this.prjCod = Number(cod);
        this.service.findByProjetoId(this.pesCod, this.prjCod).subscribe(data => {
          const { prjCod, pesCod, ...request } = data;
          this.form = request;
          this.cdr.detectChanges();
        });
      }
    });
  }

  ngOnDestroy() {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  salvar() {
    if (this.isNovo) {
      this.service.saveProjeto(this.pesCod, this.form).subscribe({
        next: data => {
          this.isNovo = false;
          this.prjCod = data.prjCod;
          this.mostrarToast('Projeto criado com sucesso!', 'sucesso');
          this.router.navigate(['/', this.pesCod, 'projetos', data.prjCod], { replaceUrl: true });
        },
        error: () => this.mostrarToast('Erro ao criar o projeto.', 'erro'),
      });
    } else {
      this.service.updateProjeto(this.pesCod, this.prjCod, this.form).subscribe({
        next: () => this.mostrarToast('Projeto atualizado com sucesso!', 'sucesso'),
        error: () => this.mostrarToast('Erro ao atualizar o projeto.', 'erro'),
      });
    }
  }

  deletar() {
    this.service.deleteProjeto(this.pesCod, this.prjCod).subscribe(() => this.voltar());
  }

  voltar() {
    this.router.navigate(['/', this.pesCod, 'projetos']);
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

  private formVazio(): ProjetoRequest {
    return { nome: '', dataInicio: '', dataTermino: '' };
  }
}
