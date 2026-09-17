import { EStatusConta } from '../usuario/usuario.model';

export interface PessoaEquipe {
  ctaCod: number;
  nome: string;
  email: string;
  papel: string | null;
  situacao: EStatusConta;
}

export interface Responsavel {
  pesCod: number;
  nome: string;
}
