import { ENivel, ERecurso } from '../../enums/autorizacao';

export interface LoginRequest {
  email: string;
  senha: string;
}

/** Matriz de permissões do papel logado (recurso → nível). Recurso ausente = NENHUM. */
export type Permissoes = Partial<Record<ERecurso, ENivel>>;

/** Contexto devolvido pelo POST /login e persistido na sessão. */
export interface LoginResponse {
  token: string;
  ctaCod: number;
  nome: string;
  email: string;
  /** Nulo para o administrador da plataforma (opera no schema público). */
  nomeSchema: string | null;
  adminPlataforma: boolean;
  papCod: number | null;
  papelNome: string | null;
  permissoes: Permissoes;
}

export interface CadastroUsuario {
  nome: string;
  email: string;
  senha: string;
  /** Preenchido no cadastro de *nova empresa* (cria o tenant). */
  nomeEmpresa?: string;
  /** Preenchido no cadastro de *empresa existente* (o e-mail deve já existir). */
  nomeSchema?: string;
}
