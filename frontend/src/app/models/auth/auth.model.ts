import { ENivel, ERecurso } from '../../enums/autorizacao';

export interface LoginRequest {
  email: string;
  senha: string;
}

export type Permissoes = Partial<Record<ERecurso, ENivel>>;

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
  nomeEmpresa?: string;
  nomeSchema?: string;
}
