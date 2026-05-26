import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PlanejamentoService } from '../../../core/services/planejamento/planajamento.service';
import { PlanejamentoDTO } from '../../../models/planejamento/planejamento.model';
import { ETipoEmpreendimento } from '../../../enums/tipo-empreendimento.enum';

@Component({
  selector: 'app-planejamento',
  templateUrl: 'planejamento.component.html',
  styleUrls: ['planejamento.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class PlanejamentoComponent implements OnInit, OnDestroy {
  planejamentos: PlanejamentoDTO[] = [];
  incCod: number | null = null;
  tipoEmpreendimento: ETipoEmpreendimento = ETipoEmpreendimento.INCUBADORA;
  modoAvaliacao = false;
  erroCarregamento: string | null = null;
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
      this.carregar();
    });
  }

  ngOnDestroy() {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  private carregar() {
    this.service.findAll(this.incCod).subscribe({
      next: data => {
        this.planejamentos = data ?? [];
        this.cdr.detectChanges();
      },
      error: err => {
        this.erroCarregamento = `Erro ao carregar: ${err.message ?? err.status ?? 'desconhecido'}`;
        this.cdr.detectChanges();
      },
    });
  }

  private prefixo(): unknown[] {
    if (this.modoAvaliacao && this.incCod != null)
      return ['/gerenciaIncubadas', this.incCod, 'planejamento'];
    if (this.incCod != null) return ['/', this.incCod, 'planejamento'];
    return ['/planejamento'];
  }

  novo() {
    this.router.navigate([...this.prefixo(), 'novo']);
  }

  editar(pesCod: number) {
    this.router.navigate([...this.prefixo(), pesCod]);
  }

  abrirProjetos(pesCod: number) {
    this.router.navigate([...this.prefixo(), pesCod, 'projetos']);
  }

  deletar(pesCod: number, nome: string) {
    if (!confirm(`Excluir planejamento "${nome}"?`)) return;
    this.service.delete(pesCod, this.incCod).subscribe({
      next: () => {
        this.planejamentos = this.planejamentos.filter(p => p.pesCod !== pesCod);
        this.mostrarToast('Planejamento excluído.', 'sucesso');
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Erro ao excluir.', 'erro'),
    });
  }

  voltar() {
    if (this.modoAvaliacao && this.incCod != null) {
      this.router.navigate(['/incubadas', this.incCod]);
    }
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
}
