import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IncubadasService } from '../../core/services/incubadas/incubadas.service';
import { IncubadaDTO } from '../../models/incubadas/incubada.model';
import { EStatusIncubacao } from '../../enums/status-incubacao.enum';
import { PessoasAbaComponent } from './abas/pessoas-aba.component';
import { AnexosComponent } from '../shared/anexos/anexos.component';
import { CepService } from '../../core/services/cep/cep.service';

type Aba = 'geral' | 'endereco' | 'pessoas';

@Component({
  selector: 'app-incubada-detalhe',
  templateUrl: 'incubada-detalhe.component.html',
  styleUrls: ['incubada-detalhe.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, PessoasAbaComponent, AnexosComponent],
})
export class IncubadaDetalheComponent implements OnInit, OnDestroy {
  form: IncubadaDTO = this.formVazio();
  incCod = 0;
  isNovo = true;
  abaAtiva: Aba = 'geral';
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  cep = '';
  buscandoCep = false;

  readonly statusOpcoes = Object.values(EStatusIncubacao);

  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private service: IncubadasService,
    private cepService: CepService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const cod = params.get('incCod');
      this.isNovo = !cod || cod === 'novo';

      if (this.isNovo) {
        this.form = this.formVazio();
        this.incCod = 0;
        this.abaAtiva = 'geral';
      } else {
        this.incCod = Number(cod);
        this.service.findById(this.incCod).subscribe(data => {
          this.form = {
            incCod: data.incCod,
            nome: data.nome,
            dataInicioIncubacao: data.dataInicioIncubacao,
            email: data.email,
            eStatusIncubacao: data.eStatusIncubacao,
            descricao: data.descricao,
            documentacao: data.documentacao,
            enderecoDTO: {
              endCod: data.enderecoDTO?.endCod ?? null,
              cidade: data.enderecoDTO?.cidade ?? '',
              rua: data.enderecoDTO?.rua ?? '',
              bairro: data.enderecoDTO?.bairro ?? '',
              numero: data.enderecoDTO?.numero ?? '',
              complemento: data.enderecoDTO?.complemento ?? '',
              estado: data.enderecoDTO?.estado ?? '',
              uf: data.enderecoDTO?.uf ?? '',
            },
          };
          this.cdr.detectChanges();
        });
      }
    });
  }

  ngOnDestroy() {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  selecionarAba(a: Aba) {
    this.abaAtiva = a;
  }

  get arquivosUrl(): string {
    return `http://localhost:8080/gerenciaIncubadas/${this.incCod}/arquivos`;
  }

  salvar() {
    if (this.isNovo) {
      this.service.save(this.form).subscribe({
        next: data => {
          this.mostrarToast('Incubada criada com sucesso!', 'sucesso');
          this.router.navigate(['/incubadas', data.incCod], { replaceUrl: true });
        },
        error: () => this.mostrarToast('Erro ao criar a incubada.', 'erro'),
      });
    } else {
      this.service.update(this.incCod, this.form).subscribe({
        next: () => this.mostrarToast('Incubada atualizada com sucesso!', 'sucesso'),
        error: () => this.mostrarToast('Erro ao atualizar a incubada.', 'erro'),
      });
    }
  }

  deletar() {
    this.service.delete(this.incCod).subscribe({
      next: () => this.voltar(),
      error: () => this.mostrarToast('Erro ao excluir a incubada.', 'erro'),
    });
  }

  voltar() {
    this.router.navigate(['/incubadas']);
  }

  abrirMonitoramento() {
    if (this.isNovo) return;
    this.router.navigate(['/gerenciaIncubadas', this.incCod, 'planejamento']);
  }

  buscarCep() {
    const limpo = this.cep.replace(/\D/g, '');
    if (limpo.length !== 8 || this.buscandoCep) return;

    this.buscandoCep = true;
    this.cdr.detectChanges();

    this.cepService.buscar(limpo).subscribe({
      next: data => {
        this.buscandoCep = false;
        if (data.erro) {
          this.mostrarToast('CEP não encontrado.', 'erro');
          this.cdr.detectChanges();
          return;
        }
        this.form.enderecoDTO = {
          ...this.form.enderecoDTO,
          rua: data.logradouro ?? '',
          bairro: data.bairro ?? '',
          cidade: data.localidade ?? '',
          estado: data.estado ?? '',
          uf: data.uf ?? '',
          complemento: data.complemento || this.form.enderecoDTO.complemento,
        };
        this.cdr.detectChanges();
      },
      error: () => {
        this.buscandoCep = false;
        this.mostrarToast('Erro ao consultar o CEP.', 'erro');
        this.cdr.detectChanges();
      },
    });
  }

  statusLabel(s: string): string {
    const map: Record<string, string> = {
      PRE_INCUBADA: 'Pré-incubada',
      INCUBADA: 'Incubada',
      GRADUADA: 'Graduada',
    };
    return map[s] ?? s;
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

  private formVazio(): IncubadaDTO {
    return {
      incCod: null,
      nome: '',
      dataInicioIncubacao: '',
      email: '',
      eStatusIncubacao: EStatusIncubacao.PRE_INCUBADA,
      descricao: '',
      documentacao: '',
      enderecoDTO: {
        endCod: null,
        cidade: '',
        rua: '',
        bairro: '',
        numero: '',
        complemento: '',
        estado: '',
        uf: '',
      },
    };
  }
}
