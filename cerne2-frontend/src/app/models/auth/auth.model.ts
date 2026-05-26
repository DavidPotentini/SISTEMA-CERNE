import { ETipoEmpreendimento } from '../../enums/tipo-empreendimento.enum';

export type Role = 'ADMIN_INCUBADORA' | 'ADMIN_INCUBADA';

export interface Empresa {
  tntCod: number;
  nomeEmpresa: string;
}

export interface LoginRequest {
  tntCod: number;
  email: string;
  senha: string;
}

export interface LoginResponse {
  usnCod: number;
  email: string;
  pessoaCod: number;
  nome: string;
  tntCod: number;
  nomeEmpresa: string;
  nomeSchema: string;
  tipoEmpreendimento: ETipoEmpreendimento;
  role: Role;
  incCod: number | null;
  token: string;
}

export interface CadastroUsuario {
  nome: string | null;
  email: string;
  cpf: string | null;
  senha: string;
  nomeEmpresa: string | null;
  tntCod: number | null;
}
