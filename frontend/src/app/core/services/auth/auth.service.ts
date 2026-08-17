import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { CadastroUsuario, LoginRequest, LoginResponse } from '../../../models/auth/auth.model';

/**
 * Fonte única da sessão. Diferente do projeto antigo, o estado é um **signal**
 * (`ChangeDetectorRef`/`detectChanges` desaparecem) e a URL vem de `environment`.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;
  private readonly storageKey = 'cerne.sessao';

  readonly sessao = signal<LoginResponse | null>(this.lerSessao());
  readonly autenticado = computed(() => this.sessao() !== null);

  login(body: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.base}/login`, body)
      .pipe(tap(resp => this.gravar(resp)));
  }

  cadastrar(body: CadastroUsuario): Observable<void> {
    return this.http.post<void>(`${this.base}/cadastro`, body);
  }

  /** Ativa uma conta convidada (e-mail do admin + senha escolhida). */
  ativarConta(body: { email: string; senha: string }): Observable<void> {
    return this.http.post<void>(`${this.base}/login/ativar`, body);
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
    this.sessao.set(null);
  }

  /** Schema do tenant — enviado no header X-Tenant pelo interceptor. */
  getTenantSchema(): string | null {
    return this.sessao()?.nomeSchema ?? null;
  }

  /** JWT — enviado no header Authorization pelo interceptor. */
  getToken(): string | null {
    return this.sessao()?.token ?? null;
  }

  isAutenticado(): boolean {
    return this.autenticado();
  }

  private gravar(resp: LoginResponse): void {
    this.sessao.set(resp);
    localStorage.setItem(this.storageKey, JSON.stringify(resp));
  }

  private lerSessao(): LoginResponse | null {
    const raw = localStorage.getItem(this.storageKey);
    return raw ? (JSON.parse(raw) as LoginResponse) : null;
  }
}
