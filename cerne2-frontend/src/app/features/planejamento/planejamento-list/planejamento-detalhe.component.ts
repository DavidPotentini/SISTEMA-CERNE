import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PlanejamentoDTO } from '../../../models/planejamento/planejamento.model';
import { PlanejamentoService } from '../../../core/services/planejamento/planajamento.service';
import { ETipoEmpreendimento } from '../../../enums/tipo-empreendimento.enum';

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
  incCod: number | null = null;
  tipoEmpreendimento: ETipoEmpreendimento = ETipoEmpreendimento.INCUBADORA;
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
      this.tipoEmpreendimento = this.incCod
        ? ETipoEmpreendimento.INCUBADA
        : ETipoEmpreendimento.INCUBADORA;

      const cod = params.get('pesCod');
      this.isNovo = !cod || cod === 'novo';

      if (this.isNovo) {
        this.form = this.formVazio();
        this.pesCod = 0;
      } else {
        this.pesCod = Number(cod);
        this.service.findById(this.pesCod, this.incCod).subscribe(data => {
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
    if (this.modoAvaliacao) return;
    this.form.vldTipoEmpreendimento = this.tipoEmpreendimento;

    if (this.isNovo) {
      this.service.save(this.form, this.incCod).subscribe({
        next: data => {
          this.mostrarToast('Planejamento criado com sucesso!', 'sucesso');
          this.router.navigate([...this.prefixo(), data.pesCod], { replaceUrl: true });
        },
        error: () => this.mostrarToast('Erro ao criar o planejamento.', 'erro'),
      });
    } else {
      this.service.update(this.pesCod, this.form, this.incCod).subscribe({
        next: () => this.mostrarToast('Planejamento atualizado com sucesso!', 'sucesso'),
        error: () => this.mostrarToast('Erro ao atualizar o planejamento.', 'erro'),
      });
    }
  }

  deletar() {
    if (this.modoAvaliacao) return;
    this.service.delete(this.pesCod, this.incCod).subscribe(() => this.voltar());
  }

  voltar() {
    this.router.navigate(this.prefixo());
  }

  private prefixo(): unknown[] {
    if (this.modoAvaliacao && this.incCod != null)
      return ['/gerenciaIncubadas', '/incubadas/', this.incCod, 'planejamento'];
    if (this.incCod != null) return ['/incubadas/', this.incCod, 'planejamento'];
    return ['/planejamento'];
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
    return { pesCod: null, nome: '', dataInicio: '', dataTermino: '', vldTipoEmpreendimento: null };
  }
}
