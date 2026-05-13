import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MetricaDTO } from '../../models/metricas/metrica.model';
import { MetricasService } from '../../core/services/metricas/metricas.service';

@Component({
  selector: 'app-metrica-list',
  templateUrl: 'metrica-list.component.html',
  styleUrls: ['metrica-list.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class MetricaListComponent implements OnInit {
  metricas: MetricaDTO[] = [];

  constructor(
    private service: MetricasService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.service.findAll().subscribe((data) => {
      this.metricas = data;
      this.cdr.detectChanges();
    });
  }

  abrir(metCod: number | null) {
    if (metCod != null) this.router.navigate(['/metricas', metCod]);
  }

  novo() {
    this.router.navigate(['/metricas/novo']);
  }

  totalIndicadores(metrica: MetricaDTO): number {
    return metrica.indicadoresMetricasDTOList?.length ?? 0;
  }
}
