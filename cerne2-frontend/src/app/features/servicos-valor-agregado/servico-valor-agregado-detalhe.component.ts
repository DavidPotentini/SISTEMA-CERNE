import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ServicoValorAgregadoDTO } from '../../models/servico-valor-agregado/servico-valor-agregado.model';
import { ServicoValorAgregadoService } from '../../core/services/servico-valor-agregado/servico-valor-agregado.service';

@Component({
  selector: 'app-servico-valor-agregado-detalhe',
  templateUrl: 'servico-valor-agregado-detalhe.component.html',
  styleUrls: ['servico-valor-agregado-detalhe.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule]
})
export class ServicoValorAgregadoDetalheComponent implements OnInit, OnDestroy {
  form: ServicoValorAgregadoDTO = this.formVazio();
  servCod = 0;
  isNovo = true;
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private service: ServicoValorAgregadoService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const cod = params.get('servCod');
      this.isNovo = !cod || cod === 'novo';

      if (this.isNovo) {
        this.form = this.formVazio();
        this.servCod = 0;
      } else {
        this.servCod = Number(cod);
        this.service.findById(this.servCod).subscribe(data => {
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
          this.mostrarToast('Serviço criado com sucesso!', 'sucesso');
          this.router.navigate(['/servicosValorAgregado', data.servCod], { replaceUrl: true });
        },
        error: () => this.mostrarToast('Erro ao criar o serviço.', 'erro'),
      });
    } else {
      this.service.update(this.servCod, this.form).subscribe({
        next: () => this.mostrarToast('Serviço atualizado com sucesso!', 'sucesso'),
        error: () => this.mostrarToast('Erro ao atualizar o serviço.', 'erro'),
      });
    }
  }

  deletar() {
    this.service.delete(this.servCod).subscribe(() => this.voltar());
  }

  voltar() {
    this.router.navigate(['/servicosValorAgregado']);
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

  private formVazio(): ServicoValorAgregadoDTO {
    return { servCod: null, servTitulo: '', servDesc: '', servCusto: 0, servCondContratacao: '', servAnexos: '' };
  }
}
