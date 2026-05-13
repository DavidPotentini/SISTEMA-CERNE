import {
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlanejamentoService } from '../../../../core/services/planejamento/planajamento.service';
import {
  EvidenciaRequest,
  EvidenciaResponse,
} from '../../../../models/planejamento/evidencia.model';

@Component({
  selector: 'app-evidencias',
  templateUrl: 'evidencias.component.html',
  styleUrls: ['evidencias.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class EvidenciasComponent implements OnInit, OnChanges, OnDestroy {
  @Input() pesCod = 0;
  @Input() prjCod = 0;
  @Input() objCod = 0;
  @Input() trfCod = 0;

  evidencias: EvidenciaResponse[] = [];
  modalAberto = false;
  form: EvidenciaRequest = this.formVazio();
  editandoCod: number | null = null;
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private service: PlanejamentoService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    if (this.trfCod) this.carregar();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['trfCod'] && this.trfCod) this.carregar();
  }

  ngOnDestroy() {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  private carregar() {
    this.service
      .findEvidencias(this.pesCod, this.prjCod, this.objCod, this.trfCod)
      .subscribe({
        next: data => {
          this.evidencias = data ?? [];
          this.cdr.detectChanges();
        },
        error: () => {
          this.evidencias = [];
          this.cdr.detectChanges();
        },
      });
  }

  abrirNova() {
    this.editandoCod = null;
    this.form = this.formVazio();
    this.modalAberto = true;
    this.cdr.detectChanges();
  }

  abrirEditar(e: EvidenciaResponse) {
    this.editandoCod = e.evdCod;
    this.form = {
      descricao: e.descricao,
      caminhoArquivo: e.caminhoArquivo,
      trfCod: this.trfCod,
    };
    this.modalAberto = true;
    this.cdr.detectChanges();
  }

  fechar() {
    this.modalAberto = false;
    this.editandoCod = null;
    this.cdr.detectChanges();
  }

  salvar() {
    const body: EvidenciaRequest = { ...this.form, trfCod: this.trfCod };

    if (this.editandoCod === null) {
      this.service
        .saveEvidencia(this.pesCod, this.prjCod, this.objCod, this.trfCod, body)
        .subscribe({
          next: () => {
            this.mostrarToast('Evidência criada com sucesso!', 'sucesso');
            this.fechar();
            this.carregar();
          },
          error: () => this.mostrarToast('Erro ao criar a evidência.', 'erro'),
        });
    } else {
      this.service
        .updateEvidencia(
          this.pesCod,
          this.prjCod,
          this.objCod,
          this.trfCod,
          this.editandoCod,
          body,
        )
        .subscribe({
          next: () => {
            this.mostrarToast('Evidência atualizada com sucesso!', 'sucesso');
            this.fechar();
            this.carregar();
          },
          error: () => this.mostrarToast('Erro ao atualizar a evidência.', 'erro'),
        });
    }
  }

  excluir(e: EvidenciaResponse) {
    this.service
      .deleteEvidencia(this.pesCod, this.prjCod, this.objCod, this.trfCod, e.evdCod)
      .subscribe({
        next: () => {
          this.evidencias = this.evidencias.filter(x => x.evdCod !== e.evdCod);
          this.mostrarToast('Evidência excluída.', 'sucesso');
          this.cdr.detectChanges();
        },
        error: () => this.mostrarToast('Erro ao excluir a evidência.', 'erro'),
      });
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

  private formVazio(): EvidenciaRequest {
    return { descricao: '', caminhoArquivo: '', trfCod: this.trfCod };
  }
}
