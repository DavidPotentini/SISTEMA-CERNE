import { ChangeDetectorRef, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CanvasService } from '../../../core/services/canvas/canvas.service';
import {
  CustomerPersonasCanvasDTO,
  personaVazia,
} from '../../../models/canvas/canvas.model';

@Component({
  selector: 'app-personas-aba',
  templateUrl: 'personas-aba.component.html',
  styleUrls: ['personas-aba.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class PersonasAbaComponent implements OnChanges {
  @Input() incCod = 0;
  @Input() ambcCod = 0;

  personas: CustomerPersonasCanvasDTO[] = [];
  carregando = false;
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  constructor(
    private service: CanvasService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnChanges(c: SimpleChanges) {
    if ((c['ambcCod'] || c['incCod']) && this.ambcCod && this.incCod) this.carregar();
  }

  carregar() {
    this.carregando = true;
    this.service.findPersonas(this.incCod, this.ambcCod).subscribe({
      next: (data) => {
        this.personas = (data ?? []).map((p) => this.normalizar(p));
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.personas = [];
        this.carregando = false;
        this.cdr.detectChanges();
      },
    });
  }

  adicionar() {
    this.personas = [...this.personas, personaVazia(this.ambcCod)];
    this.cdr.detectChanges();
  }

  adicionarAtributo(p: CustomerPersonasCanvasDTO) {
    p.atributos.itens.push({ rotulo: '', valor: '' });
    this.cdr.detectChanges();
  }

  removerAtributo(p: CustomerPersonasCanvasDTO, idx: number) {
    p.atributos.itens.splice(idx, 1);
    this.cdr.detectChanges();
  }

  salvar(p: CustomerPersonasCanvasDTO) {
    p.ambcCod = this.ambcCod;
    const acao =
      p.personaCod == null
        ? this.service.savePersona(this.incCod, this.ambcCod, p)
        : this.service.updatePersona(this.incCod, this.ambcCod, p.personaCod, p);
    acao.subscribe({
      next: (data) => {
        const idx = this.personas.indexOf(p);
        if (idx >= 0) this.personas[idx] = this.normalizar(data);
        this.mostrarToast('Persona salva.', 'sucesso');
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Erro ao salvar persona.', 'erro'),
    });
  }

  excluir(p: CustomerPersonasCanvasDTO) {
    if (p.personaCod == null) {
      this.personas = this.personas.filter((x) => x !== p);
      this.cdr.detectChanges();
      return;
    }
    if (!confirm(`Excluir a persona "${p.atributos.nome || 'sem nome'}"?`)) return;
    this.service.deletePersona(this.incCod, this.ambcCod, p.personaCod).subscribe({
      next: () => {
        this.personas = this.personas.filter((x) => x !== p);
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Erro ao excluir persona.', 'erro'),
    });
  }

  private normalizar(p: CustomerPersonasCanvasDTO): CustomerPersonasCanvasDTO {
    const atr = (p.atributos ?? {}) as any;
    return {
      ambcCod: p.ambcCod ?? this.ambcCod,
      personaCod: p.personaCod ?? null,
      atributos: {
        nome: atr.nome ?? '',
        descricao: atr.descricao ?? '',
        itens: Array.isArray(atr.itens) ? atr.itens : [],
      },
    };
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
