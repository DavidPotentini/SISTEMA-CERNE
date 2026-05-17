import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PlanejamentoDTO } from '../../../models/planejamento/planejamento.model';
import { PlanejamentoService } from '../../../core/services/planejamento/planajamento.service';

@Component({
  selector: 'app-planejamento-detalhe',
  templateUrl: 'planejamento-detalhe.component.html',
  styleUrls: ['planejamento-detalhe.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule],
})
export class PlanejamentoDetalheComponent implements OnInit, OnDestroy {
  form: PlanejamentoDTO = this.formVazio();
  pesCod = 0;
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
      const cod = params.get('pesCod');
      this.isNovo = !cod || cod === 'novo';

      if (this.isNovo) {
        this.form = this.formVazio();
        this.pesCod = 0;
      } else {
        this.pesCod = Number(cod);
        this.service.findById(this.pesCod).subscribe(data => {
          this.form = data;
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
      this.service.save(this.form).subscribe({
        next: data => {
          this.mostrarToast('Planejamento criado com sucesso!', 'sucesso');
          this.router.navigate(['/planejamento', data.pesCod], { replaceUrl: true });
        },
        error: () => this.mostrarToast('Erro ao criar o planejamento.', 'erro'),
      });
    } else {
      this.service.update(this.pesCod, this.form).subscribe({
        next: () => this.mostrarToast('Planejamento atualizado com sucesso!', 'sucesso'),
        error: () => this.mostrarToast('Erro ao atualizar o planejamento.', 'erro'),
      });
    }
  }

  deletar() {
    this.service.delete(this.pesCod).subscribe(() => this.router.navigate(['/']));
  }

  voltar() {
    this.router.navigate(['/']);
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

  private formVazio(): PlanejamentoDTO {
    return { pesCod: null, nome: '', dataInicio: '', dataTermino: '' };
  }
}
