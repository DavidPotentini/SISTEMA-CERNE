import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CanvasService } from '../../core/services/canvas/canvas.service';
import { AmbienteCanvasDTO } from '../../models/canvas/canvas.model';
import { BmcAbaComponent } from './abas/bmc-aba.component';
import { LeanAbaComponent } from './abas/lean-aba.component';
import { VpcAbaComponent } from './abas/vpc-aba.component';
import { PersonasAbaComponent } from './abas/personas-aba.component';
import { ChannelAbaComponent } from './abas/channel-aba.component';

type AbaCanvas = 'bmc' | 'lean' | 'vpc' | 'personas' | 'channel';

@Component({
  selector: 'app-ambiente-canvas-detalhe',
  templateUrl: 'ambiente-canvas-detalhe.component.html',
  styleUrls: ['ambiente-canvas-detalhe.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    BmcAbaComponent,
    LeanAbaComponent,
    VpcAbaComponent,
    PersonasAbaComponent,
    ChannelAbaComponent,
  ],
})
export class AmbienteCanvasDetalheComponent implements OnInit {
  incCod = 0;
  ambcCod = 0;
  ambiente: AmbienteCanvasDTO | null = null;
  abaAtiva: AbaCanvas = 'bmc';
  editandoDescricao = false;
  novaDescricao = '';
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  constructor(
    private service: CanvasService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe((p) => {
      this.incCod = Number(p.get('incCod'));
      this.ambcCod = Number(p.get('ambcCod'));
      this.carregar();
    });
  }

  carregar() {
    this.service.findAmbienteById(this.incCod, this.ambcCod).subscribe({
      next: (data) => {
        this.ambiente = data;
        this.novaDescricao = data.descricao;
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Não foi possível carregar o ambiente.', 'erro'),
    });
  }

  selecionarAba(a: AbaCanvas) {
    this.abaAtiva = a;
  }

  editarDescricao() {
    this.editandoDescricao = true;
  }

  salvarDescricao() {
    if (!this.ambiente) return;
    const body: AmbienteCanvasDTO = {
      ambcCod: this.ambcCod,
      descricao: this.novaDescricao.trim() || this.ambiente.descricao,
      incCod: this.incCod,
    };
    this.service.updateAmbiente(this.incCod, this.ambcCod, body).subscribe({
      next: (data) => {
        this.ambiente = data;
        this.editandoDescricao = false;
        this.mostrarToast('Ambiente atualizado.', 'sucesso');
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Erro ao atualizar.', 'erro'),
    });
  }

  excluir() {
    if (!confirm('Excluir este ambiente? Todos os canvas associados serão removidos.')) return;
    this.service.deleteAmbiente(this.incCod, this.ambcCod).subscribe(() => this.voltar());
  }

  voltar() {
    this.router.navigate(['/incubadas', this.incCod, 'ambientesCanvas']);
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
