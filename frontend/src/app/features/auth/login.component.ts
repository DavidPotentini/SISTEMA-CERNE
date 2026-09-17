import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '../../core/services/auth/auth.service';

type Modo = 'entrar' | 'ativar';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly modo = signal<Modo>('entrar');
  readonly email = signal('');
  readonly senha = signal('');
  readonly confirmar = signal('');
  readonly erro = signal<string | null>(null);
  readonly sucesso = signal<string | null>(null);
  readonly carregando = signal(false);

  trocar(modo: Modo): void {
    this.modo.set(modo);
    this.erro.set(null);
    this.sucesso.set(null);
    this.senha.set('');
    this.confirmar.set('');
  }

  entrar(): void {
    this.erro.set(null);
    this.sucesso.set(null);
    this.carregando.set(true);
    this.auth.login({ email: this.email(), senha: this.senha() }).subscribe({
      next: r =>
        this.router.navigate([r.adminPlataforma ? '/admin/painel' : '/incubadora/minha-incubadora']),
      error: e => {
        this.erro.set(e?.error?.mensagem ?? e?.error?.message ?? 'E-mail ou senha inválidos.');
        this.carregando.set(false);
      },
    });
  }

  ativar(): void {
    this.erro.set(null);
    this.sucesso.set(null);
    if (this.senha().length < 6) {
      this.erro.set('A senha deve ter ao menos 6 caracteres.');
      return;
    }
    if (this.senha() !== this.confirmar()) {
      this.erro.set('As senhas não conferem.');
      return;
    }
    this.carregando.set(true);
    this.auth.ativarConta({ email: this.email(), senha: this.senha() }).subscribe({
      next: () => {
        this.carregando.set(false);
        this.modo.set('entrar');
        this.senha.set('');
        this.confirmar.set('');
        this.sucesso.set('Conta ativada! Faça login com sua nova senha.');
      },
      error: e => {
        this.erro.set(e?.error?.mensagem ?? e?.error?.message ?? 'Não foi possível ativar a conta.');
        this.carregando.set(false);
      },
    });
  }
}
