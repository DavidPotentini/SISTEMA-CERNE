import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MetodologiaService } from '../../core/services/metodologia/metodologia.service';
import { Versao } from '../../models/metodologia/metodologia.model';

/**
 * Aba "Publicação": mostra se o rascunho tem alterações não publicadas, permite publicar (clona a
 * árvore numa nova versão VIGENTE) e lista o histórico de publicações.
 */
@Component({
  selector: 'app-publicacao-tab',
  imports: [MatCardModule, MatButtonModule, MatIconModule, MatTableModule],
  templateUrl: './publicacao-tab.component.html',
  styleUrl: './publicacao-tab.component.css',
})
export class PublicacaoTabComponent {
  private readonly service = inject(MetodologiaService);

  readonly colunas = ['versao', 'data', 'autor'];
  readonly publicando = signal(false);
  readonly erro = signal<string | null>(null);

  /** Reagem a mutações (em qualquer aba) e à própria publicação via o sinal `versao`. */
  readonly rascunho = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.versaoDeTrabalho(),
  });
  readonly historico = rxResource({
    params: () => ({ v: this.service.versao() }),
    stream: () => this.service.listarVersoes(),
  });

  readonly alterada = computed(() => this.rascunho.value()?.alterada ?? false);

  publicar(): void {
    if (!this.alterada()) return;
    this.publicando.set(true);
    this.erro.set(null);
    this.service.publicar().subscribe({
      next: () => {
        this.publicando.set(false);
        this.service.recarregar();
      },
      error: e => {
        this.publicando.set(false);
        this.erro.set(e?.error?.mensagem ?? 'Falha ao publicar.');
      },
    });
  }

  dataPub(v: Versao): string {
    return v.publicadaEm ? new Date(v.publicadaEm).toLocaleDateString('pt-BR') : '—';
  }
}
