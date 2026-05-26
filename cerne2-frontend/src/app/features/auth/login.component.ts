import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth/auth.service';
import { Empresa } from '../../models/auth/auth.model';

@Component({
  selector: 'app-login',
  templateUrl: 'login.component.html',
  styleUrls: ['login.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
})
export class LoginComponent implements OnInit, OnDestroy {
  empresas: Empresa[] = [];
  tntCod: number | null = null;
  email = '';
  senha = '';
  carregando = false;
  toast: { texto: string; tipo: 'sucesso' | 'erro' } | null = null;

  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.authService.listarEmpresas().subscribe({
      next: data => {
        this.empresas = data ?? [];
        this.cdr.detectChanges();
      },
      error: () => this.mostrarToast('Não foi possível carregar as empresas.', 'erro'),
    });
  }

  ngOnDestroy() {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  entrar() {
    if (this.tntCod == null || !this.email || !this.senha) {
      this.mostrarToast('Preencha empresa, e-mail e senha.', 'erro');
      return;
    }

    this.carregando = true;
    this.authService
      .login({ tntCod: this.tntCod, email: this.email, senha: this.senha })
      .subscribe({
        next: () => {
          this.carregando = false;
          this.router.navigate(['/incubadas']);
        },
        error: err => {
          this.carregando = false;
          this.mostrarToast(this.mensagemErro(err), 'erro');
        },
      });
  }

  private mensagemErro(err: { status?: number }): string {
    if (err?.status === 401) return 'E-mail ou senha inválidos.';
    if (err?.status === 403) return 'E-mail não cadastrado nesta empresa. Contate o administrador.';
    if (err?.status === 404) return 'Empresa não encontrada.';
    return 'Não foi possível entrar. Tente novamente.';
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
