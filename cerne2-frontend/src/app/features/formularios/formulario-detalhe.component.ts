import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {
  Campo,
  FormularioConfig,
  FormularioDTO,
} from '../../models/formularios/formulario.model';
import { FormulariosService } from '../../core/services/formularios/formularios.service';
import { FormularioCamposEditorComponent } from './formulario-campos-editor.component';

@Component({
  selector: 'app-formulario-detalhe',
  templateUrl: 'formulario-detalhe.component.html',
  styleUrls: ['formulario-detalhe.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule, FormularioCamposEditorComponent],
})
export class FormularioDetalheComponent implements OnInit, OnDestroy {
  form: FormularioDTO = this.formVazio();

  frmCod = 0;
  isNovo = true;
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private service: FormulariosService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const cod = params.get('frmCod');
      this.isNovo = !cod || cod === 'novo';

      if (this.isNovo) {
        this.form = this.formVazio();
        this.frmCod = 0;
        this.cdr.detectChanges();
        return;
      }

      this.frmCod = Number(cod);
      this.service.findByFrmCod(this.frmCod).subscribe((formulario) => {
        this.form = {
          ...formulario,
          jsonConfig: this.normalizarConfig(formulario.jsonConfig),
        };
        this.cdr.detectChanges();
      });
    });
  }

  ngOnDestroy() {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  get campos(): Campo[] {
    return this.form.jsonConfig.campos;
  }

  salvar() {
    const acao = this.isNovo
      ? this.service.save(this.form)
      : this.service.update(this.frmCod, this.form);

    acao.subscribe({
      next: (data) => {
        this.mostrarToast(
          this.isNovo ? 'Formulário criado com sucesso!' : 'Formulário atualizado com sucesso!',
          'sucesso',
        );
        if (this.isNovo && data.frmCod != null) {
          this.router.navigate(['/formularios', data.frmCod], { replaceUrl: true });
        }
      },
      error: () => this.mostrarToast('Erro ao salvar o formulário.', 'erro'),
    });
  }

  deletar() {
    this.service.delete(this.frmCod).subscribe(() => this.voltar());
  }

  voltar() {
    this.router.navigate(['/formularios']);
  }

  private formVazio(): FormularioDTO {
    return { frmCod: null, nome: '', proposito: '', jsonConfig: { campos: [] } };
  }

  private normalizarConfig(raw: unknown): FormularioConfig {
    if (raw && typeof raw === 'object' && Array.isArray((raw as { campos?: unknown }).campos)) {
      return raw as FormularioConfig;
    }
    return { campos: [] };
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
