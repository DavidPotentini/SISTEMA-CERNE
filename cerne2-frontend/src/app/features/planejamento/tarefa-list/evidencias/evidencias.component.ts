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
import { EvidenciaDTO } from '../../../../models/planejamento/evidencia.model';
import { AnexosComponent } from '../../../shared/anexos/anexos.component';

@Component({
  selector: 'app-evidencias',
  templateUrl: 'evidencias.component.html',
  styleUrls: ['evidencias.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, AnexosComponent],
})
export class EvidenciasComponent implements OnInit, OnChanges, OnDestroy {
  @Input() pesCod = 0;
  @Input() prjCod = 0;
  @Input() objCod = 0;
  @Input() trfCod = 0;
  @Input() incCod: number | null = null;

  evidencias: EvidenciaDTO[] = [];
  modalAberto = false;
  anexosDe: EvidenciaDTO | null = null;
  form: EvidenciaDTO = this.formVazio();
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
      .findEvidencias(this.pesCod, this.prjCod, this.objCod, this.trfCod, this.incCod)
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

  abrirEditar(e: EvidenciaDTO) {
    this.editandoCod = e.evdCod;
    this.form = {
      evdCod: e.evdCod,
      descricao: e.descricao,
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

  abrirAnexos(e: EvidenciaDTO) {
    this.anexosDe = e;
    this.cdr.detectChanges();
  }

  fecharAnexos() {
    this.anexosDe = null;
    this.cdr.detectChanges();
  }

  arquivosUrlEvidencia(evdCod: number): string {
    return this.evidenciaBase(evdCod) + '/arquivos';
  }

  arquivoBaseUrlEvidencia(evdCod: number): string {
    return this.evidenciaBase(evdCod) + '/arquivos';
  }

  private evidenciaBase(evdCod: number): string {
    const host = 'http://localhost:8080';
    const base = this.incCod
      ? `${host}/incubadas/${this.incCod}/planejamento`
      : `${host}/planejamento`;
    return (
      `${base}/${this.pesCod}/projetos/${this.prjCod}/objetivos/${this.objCod}` +
      `/tarefas/${this.trfCod}/evidencias/${evdCod}`
    );
  }

  salvar() {
    const body: EvidenciaDTO = { ...this.form, trfCod: this.trfCod };

    if (this.editandoCod === null) {
      this.service
        .saveEvidencia(this.pesCod, this.prjCod, this.objCod, this.trfCod, body, this.incCod)
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
          this.incCod
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

  excluir(e: EvidenciaDTO) {
    this.service
      .deleteEvidencia(this.pesCod, this.prjCod, this.objCod, this.trfCod, e.evdCod!, this.incCod)
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

  private formVazio(): EvidenciaDTO {
    return { evdCod: null, descricao: '', trfCod: this.trfCod };
  }
}
