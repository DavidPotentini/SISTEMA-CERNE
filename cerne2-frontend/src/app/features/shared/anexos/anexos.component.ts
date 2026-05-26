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
import { ArquivoService } from '../../../core/services/arquivo/arquivo.service';
import { ArquivoDTO } from '../../../models/arquivo/arquivo.model';

/**
 * Componente reutilizável de anexos. Recebe as URLs prontas dos endpoints de
 * arquivo (que ficam aninhados nos controllers de incubadas / evidências).
 */
@Component({
  selector: 'app-anexos',
  templateUrl: 'anexos.component.html',
  styleUrls: ['anexos.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class AnexosComponent implements OnInit, OnChanges, OnDestroy {
  /** Endpoint `.../arquivos` (GET lista, POST upload). */
  @Input() arquivosUrl = '';
  /** Base de `.../arquivos/{arqCod}` (GET download, DELETE). */
  @Input() arquivoBaseUrl = '';

  arquivos: ArquivoDTO[] = [];
  carregando = false;
  enviando = false;
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private arquivoService: ArquivoService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    if (this.arquivosUrl) this.carregar();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['arquivosUrl'] && this.arquivosUrl) this.carregar();
  }

  ngOnDestroy() {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  private carregar() {
    this.carregando = true;
    this.arquivoService.listar(this.arquivosUrl).subscribe({
      next: data => {
        this.arquivos = data ?? [];
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.arquivos = [];
        this.carregando = false;
        this.cdr.detectChanges();
      },
    });
  }

  selecionarArquivo(event: Event) {
    const input = event.target as HTMLInputElement;
    const arquivo = input.files?.[0];
    if (!arquivo) return;

    this.enviando = true;
    this.cdr.detectChanges();

    this.arquivoService.upload(this.arquivosUrl, arquivo).subscribe({
      next: salvo => {
        this.arquivos = [...this.arquivos, salvo];
        this.enviando = false;
        input.value = '';
        this.mostrarToast('Arquivo enviado com sucesso!', 'sucesso');
        this.cdr.detectChanges();
      },
      error: () => {
        this.enviando = false;
        input.value = '';
        this.mostrarToast('Erro ao enviar o arquivo.', 'erro');
        this.cdr.detectChanges();
      },
    });
  }

  baixar(arquivo: ArquivoDTO) {
    this.arquivoService.urlDownload(this.arquivoBaseUrl, arquivo.arqCod).subscribe({
      next: url => window.open(url, '_blank'),
      error: () => this.mostrarToast('Erro ao gerar o link de download.', 'erro'),
    });
  }

  excluir(arquivo: ArquivoDTO) {
    this.arquivoService.excluir(this.arquivoBaseUrl, arquivo.arqCod).subscribe({
      next: () => {
        this.arquivos = this.arquivos.filter(a => a.arqCod !== arquivo.arqCod);
        this.mostrarToast('Arquivo excluído.', 'sucesso');
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Erro ao excluir o arquivo.', 'erro'),
    });
  }

  formatarTamanho(bytes: number): string {
    if (bytes == null) return '';
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
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
