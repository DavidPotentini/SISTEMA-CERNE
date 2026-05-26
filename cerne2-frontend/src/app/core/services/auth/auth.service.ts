import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import {
  CadastroUsuario,
  Empresa,
  LoginRequest,
  LoginResponse,
} from '../../../models/auth/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private base = 'http://localhost:8080';
  private readonly storageKey = 'cerne2.sessao';
  private sessao: LoginResponse | null = this.lerSessao();

  constructor(private http: HttpClient) {}

  /** Empresas (tenants) para o seletor da tela de login. */
  listarEmpresas(): Observable<Empresa[]> {
    return this.http.get<Empresa[]>(`${this.base}/login/empresas`);
  }

  login(body: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.base}/login`, body)
      .pipe(tap(resp => this.gravarSessao(resp)));
  }

  cadastrar(body: CadastroUsuario): Observable<void> {
    return this.http.post<void>(`${this.base}/cadastro`, body);
  }

  logout(): void {
    this.sessao = null;
    localStorage.removeItem(this.storageKey);
  }

  getSessao(): LoginResponse | null {
    return this.sessao;
  }

  /** Schema do tenant atual — enviado no header X-Tenant pelo interceptor. */
  getTenantSchema(): string | null {
    return this.sessao?.nomeSchema ?? null;
  }

  /** JWT atual — enviado no header Authorization pelo interceptor. */
  getToken(): string | null {
    return this.sessao?.token ?? null;
  }

  isAutenticado(): boolean {
    return this.sessao !== null;
  }

  private gravarSessao(resp: LoginResponse): void {
    this.sessao = resp;
    localStorage.setItem(this.storageKey, JSON.stringify(resp));
  }

  private lerSessao(): LoginResponse | null {
    const raw = localStorage.getItem(this.storageKey);
    return raw ? (JSON.parse(raw) as LoginResponse) : null;
  }
}
