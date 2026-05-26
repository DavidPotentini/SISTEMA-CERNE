import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth/auth.service';
import { CadastroUsuario, Empresa } from '../../models/auth/auth.model';

type ModoCadastro = 'nova' | 'existente';

@Component({
  selector: 'app-cadastro',
  templateUrl: 'cadastro.component.html',
  styleUrls: ['cadastro.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
})
export class CadastroComponent implements OnInit, OnDestroy {
  modo: ModoCadastro = 'nova';

  nome = '';
  cpf = '';
  email = '';
  senha = '';
  nomeEmpresa = '';
  tntCod: number | null = null;

  empresas: Empresa[] = [];
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
      error: () => {},
    });
  }

  ngOnDestroy() {
    if (this.toastTimer) clearTimeout(this.toastTimer);
  }

  selecionarModo(modo: ModoCadastro) {
    this.modo = modo;
  }

  cadastrar() {
    const body = this.montarBody();
    if (!body) return;

    this.carregando = true;
    this.authService.cadastrar(body).subscribe({
      next: () => {
        this.carregando = false;
        this.mostrarToast('Cadastro realizado! Faça login para entrar.', 'sucesso');
        setTimeout(() => this.router.navigate(['/login']), 1200);
      },
      error: err => {
        this.carregando = false;
        this.mostrarToast(this.mensagemErro(err), 'erro');
      },
    });
  }

  private montarBody(): CadastroUsuario | null {
    if (!this.email || !this.senha) {
      this.mostrarToast('Informe e-mail e senha.', 'erro');
      return null;
    }

    if (this.modo === 'nova') {
      if (!this.nome || !this.cpf || !this.nomeEmpresa) {
        this.mostrarToast('Preencha nome, CPF e nome da empresa.', 'erro');
        return null;
      }
      return {
        nome: this.nome,
        email: this.email,
        cpf: this.cpf,
        senha: this.senha,
        nomeEmpresa: this.nomeEmpresa,
        tntCod: null,
      };
    }

    if (this.tntCod == null) {
      this.mostrarToast('Selecione a empresa.', 'erro');
      return null;
    }
    return {
      nome: null,
      email: this.email,
      cpf: null,
      senha: this.senha,
      nomeEmpresa: null,
      tntCod: this.tntCod,
    };
  }

  private mensagemErro(err: { status?: number }): string {
    if (err?.status === 403) {
      return 'E-mail não cadastrado na empresa. Contate o administrador.';
    }
    if (err?.status === 404) return 'Empresa não encontrada.';
    return 'Não foi possível concluir o cadastro. Tente novamente.';
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
