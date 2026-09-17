import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { EquipeService } from '../../core/services/equipe/equipe.service';
import { dataParaIso, isoParaData } from '../../shared/util/data';
import { PlanejamentoService } from '../../core/services/planejamento/planejamento.service';
import { AtividadePlanejada } from '../../models/planejamento/planejamento.model';
import { EmpreendimentoService } from '../../core/services/empreendimento/empreendimento.service';

interface AtividadePlanejadaFormData {
  prtcCod: number;
  pratica: string;
  atividade?: AtividadePlanejada;
}

@Component({
  selector: 'app-atividade-planejada-form',
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
  ],
  templateUrl: './atividade-planejada-form.dialog.html',
  styleUrl: './atividade-planejada-form.dialog.css',
})
export class AtividadePlanejadaFormDialog {
  private readonly service = inject(PlanejamentoService);
  private readonly equipeService = inject(EquipeService);
  private readonly empreendimentoService = inject(EmpreendimentoService);
  private readonly ref = inject(MatDialogRef<AtividadePlanejadaFormDialog>);
  private readonly data = inject<AtividadePlanejadaFormData>(MAT_DIALOG_DATA);

  readonly pratica = this.data.pratica;
  readonly edicao = this.data.atividade != null;

  readonly SEM_VALOR = -1;

  readonly responsaveisRes = rxResource({
    stream: () => this.equipeService.listarResponsaveis(),
  });

  readonly empreedimentosRes = rxResource({
    stream: () => this.empreendimentoService.listarDoCiclo(),
  });

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  readonly nome = signal(this.data.atividade?.nome ?? '');
  readonly observacoes = signal(this.data.atividade?.observacoes ?? '');
  readonly respPesCod = signal<number | null>(this.data.atividade?.respPesCod ?? null);
  readonly empCod = signal<number | null>(this.data.atividade?.empCod ?? null);
  readonly prazo = signal(this.data.atividade?.prazo ?? '');

  protected readonly isoParaData = isoParaData;
  protected readonly dataParaIso = dataParaIso;

  salvar(): void {
    if (!this.nome().trim()) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<AtividadePlanejada> = {
      nome: this.nome().trim(),
      observacoes: this.observacoes().trim() || null,
      respPesCod: this.respPesCod(),
      empCod: this.empCod(),
      prazo: this.prazo() || null,
    };
    const req = this.data.atividade
      ? this.service.ajustarAtividade(this.data.atividade.atpCod, dto)
      : this.service.adicionarComplementar(this.data.prtcCod, dto);
    req.subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao salvar a atividade.');
      },
    });
  }
}
