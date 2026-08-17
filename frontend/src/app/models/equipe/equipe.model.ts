import { EStatusConta } from '../usuario/usuario.model';

/** Linha da "Equipe vinculada" da incubadora. */
export interface PessoaEquipe {
  ctaCod: number;
  nome: string;
  email: string;
  /** Papel local na incubadora, ou {@code null} ("—"). */
  papel: string | null;
  situacao: EStatusConta;
}
