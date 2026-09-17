import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { ArquivoService } from '../../core/services/arquivo/arquivo.service';
import { EvidenciaService } from '../../core/services/evidencia/evidencia.service';
import { AtividadeOpcao, Evidencia } from '../../models/evidencia/evidencia.model';

interface EvidenciaFormData {
  modo: 'registrar' | 'corrigir';
  evidencia?: Evidencia;
}

interface Opcao {
  cod: number;
  nome: string | null;
}

@Component({
  selector: 'app-evidencia-form',
  imports: [
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatProgressBarModule,
  ],
  templateUrl: './evidencia-form.dialog.html',
  styleUrl: './evidencia-form.dialog.css',
})
export class EvidenciaFormDialog {
  private readonly service = inject(EvidenciaService);
  private readonly arquivoService = inject(ArquivoService);
  private readonly ref = inject(MatDialogRef<EvidenciaFormDialog>);
  private readonly data = inject<EvidenciaFormData>(MAT_DIALOG_DATA);

  readonly corrigindo = this.data.modo === 'corrigir';

  readonly carregando = signal(true);
  readonly opcoes = signal<AtividadeOpcao[]>([]);
  readonly prcCod = signal<number | null>(null);
  readonly prtCod = signal<number | null>(null);
  readonly atpCod = signal<number | null>(this.data.evidencia?.atpCod ?? null);

  readonly processos = computed<Opcao[]>(() => {
    const mapa = new Map<number, Opcao>();
    for (const o of this.opcoes()) {
      if (!mapa.has(o.prccCod)) mapa.set(o.prccCod, { cod: o.prccCod, nome: o.processoNome });
    }
    return [...mapa.values()];
  });

  readonly praticas = computed<Opcao[]>(() => {
    const mapa = new Map<number, Opcao>();
    for (const o of this.opcoes()) {
      if (o.prccCod !== this.prcCod()) continue;
      if (!mapa.has(o.prtcCod)) mapa.set(o.prtcCod, { cod: o.prtcCod, nome: o.praticaNome });
    }
    return [...mapa.values()];
  });

  readonly atividades = computed<AtividadeOpcao[]>(() =>
    this.opcoes().filter(o => o.prtcCod === this.prtCod()),
  );

  readonly titulo = signal(this.data.evidencia?.titulo ?? '');
  readonly motivoCorrecao = this.data.evidencia?.motivoCorrecao ?? null;
  readonly arqCod = signal<number | null>(this.data.evidencia?.arqCod ?? null);
  readonly arquivoNome = signal<string | null>(this.data.evidencia?.arquivoNome ?? null);
  readonly enviandoArquivo = signal(false);

  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);

  constructor() {
    this.service.atividades().subscribe({
      next: lista => {
        this.opcoes.set(lista);
        this.preselecionar(lista);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar as atividades do plano vigente.');
        this.carregando.set(false);
      },
    });
  }

  private preselecionar(lista: AtividadeOpcao[]): void {
    const atp = this.atpCod();
    const atual = atp == null ? undefined : lista.find(o => o.atpCod === atp);
    if (atual) {
      this.prcCod.set(atual.prccCod);
      this.prtCod.set(atual.prtcCod);
    }
  }

  processoAlterado(): void {
    this.prtCod.set(null);
    this.atpCod.set(null);
  }

  praticaAlterada(): void {
    this.atpCod.set(null);
  }

  selecionarArquivo(evento: Event): void {
    const input = evento.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    this.enviandoArquivo.set(true);
    this.erro.set(null);
    this.arquivoService.upload(file).subscribe({
      next: arq => {
        this.arqCod.set(arq.arqCod);
        this.arquivoNome.set(arq.nomeOriginal);
        this.enviandoArquivo.set(false);
      },
      error: e => {
        this.enviandoArquivo.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao enviar o arquivo.');
      },
    });
  }

  removerArquivo(): void {
    this.arqCod.set(null);
    this.arquivoNome.set(null);
  }

  get podeSalvar(): boolean {
    return (
      !!this.titulo().trim() && this.atpCod() != null && !this.enviandoArquivo() && !this.salvando()
    );
  }

  salvar(): void {
    if (!this.podeSalvar) return;
    this.salvando.set(true);
    this.erro.set(null);
    const dto: Partial<Evidencia> = {
      titulo: this.titulo().trim(),
      atpCod: this.atpCod()!,
      arqCod: this.arqCod(),
    };
    const req = this.corrigindo
      ? this.service.corrigir(this.data.evidencia!.evdCod, dto)
      : this.service.registrar(dto);
    req.subscribe({
      next: () => {
        this.service.recarregar();
        this.ref.close(true);
      },
      error: e => {
        this.salvando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao salvar a evidência.');
      },
    });
  }
}
