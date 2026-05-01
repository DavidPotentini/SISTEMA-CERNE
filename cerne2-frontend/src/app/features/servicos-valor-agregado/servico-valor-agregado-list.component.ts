import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ServicoValorAgregadoResponse } from '../../models/servico-valor-agregado/servico-valor-agregado.model';
import { ServicoValorAgregadoService } from '../../core/services/servico-valor-agregado/servico-valor-agregado.service';

@Component({
  selector: 'app-servico-valor-agregado-list',
  templateUrl: 'servico-valor-agregado-list.component.html',
  styleUrls: ['servico-valor-agregado-list.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class ServicoValorAgregadoListComponent implements OnInit {
  servicos: ServicoValorAgregadoResponse[] = [];

  constructor(
    private service: ServicoValorAgregadoService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.service.findAll().subscribe(data => {
      this.servicos = data;
      this.cdr.detectChanges();
    });
  }

  abrir(servCod: number) {
    this.router.navigate(['/servicosValorAgregado', servCod]);
  }

  novo() {
    this.router.navigate(['/servicosValorAgregado/novo']);
  }
}
