import { ChangeDetectorRef, Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Campo, TipoCampo } from '../../models/formularios/formulario.model';

@Component({
  selector: 'app-formulario-campos-editor',
  templateUrl: 'formulario-campos-editor.component.html',
  styleUrls: ['formulario-campos-editor.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class FormularioCamposEditorComponent {
  @Input() campos: Campo[] = [];

  readonly tipos: { valor: TipoCampo; rotulo: string }[] = [
    { valor: 'texto', rotulo: 'Texto' },
    { valor: 'numero', rotulo: 'Número' },
    { valor: 'data', rotulo: 'Data' },
    { valor: 'select', rotulo: 'Lista (seleção única)' },
    { valor: 'checkbox', rotulo: 'Múltipla escolha' },
  ];

  constructor(private cdr: ChangeDetectorRef) {}

  adicionarCampo() {
    this.campos.push({ nome: '', tipo: 'texto', obrigatorio: false });
    this.cdr.detectChanges();
  }

  removerCampo(idx: number) {
    this.campos.splice(idx, 1);
    this.cdr.detectChanges();
  }

  trocarTipo(campo: Campo, novoTipo: TipoCampo) {
    campo.tipo = novoTipo;
    if (this.precisaOpcoes(novoTipo)) {
      if (!campo.opcoes) campo.opcoes = [''];
    } else {
      delete campo.opcoes;
    }
    this.cdr.detectChanges();
  }

  precisaOpcoes(tipo: TipoCampo): boolean {
    return tipo === 'select' || tipo === 'checkbox';
  }

  adicionarOpcao(campo: Campo) {
    if (!campo.opcoes) campo.opcoes = [];
    campo.opcoes.push('');
    this.cdr.detectChanges();
  }

  removerOpcao(campo: Campo, opIdx: number) {
    campo.opcoes?.splice(opIdx, 1);
    this.cdr.detectChanges();
  }

  atualizarOpcao(campo: Campo, idx: number, valor: string) {
    if (campo.opcoes) campo.opcoes[idx] = valor;
  }

  trackByIndex(index: number) {
    return index;
  }
}
