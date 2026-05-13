import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormularioDTO } from '../../models/formularios/formulario.model';
import { FormulariosService } from '../../core/services/formularios/formularios.service';

@Component({
  selector: 'app-formulario-list',
  templateUrl: 'formulario-list.component.html',
  styleUrls: ['formulario-list.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class FormularioListComponent implements OnInit {
  formularios: FormularioDTO[] = [];

  constructor(
    private service: FormulariosService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.service.findAll().subscribe((data) => {
      this.formularios = data;
      this.cdr.detectChanges();
    });
  }

  abrir(frmCod: number | null) {
    if (frmCod != null) this.router.navigate(['/formularios', frmCod]);
  }

  novo() {
    this.router.navigate(['/formularios/novo']);
  }

  totalCampos(formulario: FormularioDTO): number {
    return formulario.jsonConfig ? Object.keys(formulario.jsonConfig).length : 0;
  }
}
