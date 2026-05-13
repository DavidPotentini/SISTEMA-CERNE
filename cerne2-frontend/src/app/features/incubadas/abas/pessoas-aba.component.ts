import {
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PessoasService } from '../../../core/services/pessoas/pessoas.service';
import { PessoaResponse } from '../../../models/pessoas/pessoa.model';

@Component({
  selector: 'app-incubada-pessoas-aba',
  templateUrl: 'pessoas-aba.component.html',
  styleUrls: ['pessoas-aba.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class PessoasAbaComponent implements OnInit, OnChanges {
  @Input() incCod = 0;

  pessoas: PessoaResponse[] = [];
  carregando = true;

  constructor(
    private pessoasService: PessoasService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    if (this.incCod) this.carregar();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['incCod'] && this.incCod) this.carregar();
  }

  private carregar() {
    this.carregando = true;
    this.pessoasService.findByIncubada(this.incCod).subscribe({
      next: data => {
        this.pessoas = data ?? [];
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.pessoas = [];
        this.carregando = false;
        this.cdr.detectChanges();
      },
    });
  }

  abrir(pessoaCod: number) {
    this.router.navigate(['/pessoas', pessoaCod]);
  }
}
