import { ETipoEmpreendimento } from '../../enums/tipo-empreendimento.enum';

export interface PessoaResponse {
  pessoaCod: number;
  nome: string;
  email: string;
  cpf: string;
  cargo: string;
  incCod: number | null;
  eTipoEmpreendimento: ETipoEmpreendimento;
}

export interface PessoaRequest {
  nome: string;
  email: string;
  cpf: string;
  cargo: string;
  incCod: number | null;
  eTipoEmpreendimento: ETipoEmpreendimento;
}
