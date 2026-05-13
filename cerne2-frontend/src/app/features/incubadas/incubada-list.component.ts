import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { IncubadasService } from '../../core/services/incubadas/incubadas.service';
import { IncubadaResponse } from '../../models/incubadas/incubada.model';

@Component({
  selector: 'app-incubada-list',
  templateUrl: 'incubada-list.component.html',
  styleUrls: ['incubada-list.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class IncubadaListComponent implements OnInit {
  incubadas: IncubadaResponse[] = [];
  carregando = true;

  constructor(
    private service: IncubadasService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.service.findAll().subscribe({
      next: data => {
        this.incubadas = data ?? [];
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.incubadas = [];
        this.carregando = false;
        this.cdr.detectChanges();
      },
    });
  }

  abrir(incCod: number) {
    this.router.navigate(['/incubadas', incCod]);
  }

  novo() {
    this.router.navigate(['/incubadas/novo']);
  }

  statusLabel(s: string): string {
    const map: Record<string, string> = {
      PRE_INCUBADA: 'Pré-incubada',
      INCUBADA: 'Incubada',
      GRADUADA: 'Graduada',
    };
    return map[s] ?? s;
  }
}
