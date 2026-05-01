import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PessoaResponse } from '../../models/pessoas/pessoa.model';
import { PessoasService } from '../../core/services/pessoas/pessoas.service';

@Component({
  selector: 'app-pessoa-list',
  templateUrl: 'pessoa-list.component.html',
  styleUrls: ['pessoa-list.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class PessoaListComponent implements OnInit {
  pessoas: PessoaResponse[] = [];
  carregando = true;

  constructor(
    private service: PessoasService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.service.findByIncubadora().subscribe({
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

  novo() {
    this.router.navigate(['/pessoas/novo']);
  }
}
