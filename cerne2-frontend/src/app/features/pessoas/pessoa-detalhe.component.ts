import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PessoaRequest } from '../../models/pessoas/pessoa.model';
import { PessoasService } from '../../core/services/pessoas/pessoas.service';
import { ETipoEmpreendimento } from '../../enums/tipo-empreendimento.enum';

@Component({
  selector: 'app-pessoa-detalhe',
  templateUrl: 'pessoa-detalhe.component.html',
  styleUrls: ['pessoa-detalhe.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule],
})
export class PessoaDetalheComponent implements OnInit, OnDestroy {
  form: PessoaRequest = this.formVazio();
  pessoaCod = 0;
  isNovo = true;
  tiposEmpreendimento = Object.values(ETipoEmpreendimento);
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private service: PessoasService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const cod = params.get('pessoaCod');
      this.isNovo = !cod || cod === 'novo';

      if (this.isNovo) {
        this.form = this.formVazio();
        this.pessoaCod = 0;
      } else {
        this.pessoaCod = Number(cod);
        this.service.findById(this.pessoaCod).subscribe(data => {
          const { pessoaCod, ...request } = data;
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
      this.service.save(this.form).subscribe({
        next: data => {
          this.mostrarToast('Pessoa criada com sucesso!', 'sucesso');
          this.router.navigate(['/pessoas', data.pessoaCod], { replaceUrl: true });
        },
        error: () => this.mostrarToast('Erro ao criar a pessoa.', 'erro'),
      });
    } else {
      this.service.update(this.pessoaCod, this.form).subscribe({
        next: () => this.mostrarToast('Pessoa atualizada com sucesso!', 'sucesso'),
        error: () => this.mostrarToast('Erro ao atualizar a pessoa.', 'erro'),
      });
    }
  }

  deletar() {
    this.service.delete(this.pessoaCod).subscribe(() => this.voltar());
  }

  voltar() {
    this.router.navigate(['/pessoas']);
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

  private formVazio(): PessoaRequest {
    return {
      nome: '',
      email: '',
      cpf: '',
      cargo: '',
      incCod: null,
      eTipoEmpreendimento: ETipoEmpreendimento.INCUBADORA,
    };
  }
}
