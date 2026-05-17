import { ETipoEmpreendimento } from '../../enums/tipo-empreendimento.enum';

export interface PessoaDTO {
  pessoaCod: number | null;
  nome: string;
  email: string;
  cpf: string;
  cargo: string;
  incCod: number | null;
  eTipoEmpreendimento: ETipoEmpreendimento;
}
