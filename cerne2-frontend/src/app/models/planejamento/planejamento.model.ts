import { ETipoEmpreendimento } from '../../enums/tipo-empreendimento.enum';

export interface PlanejamentoDTO {
  pesCod: number | null;
  nome: string;
  dataInicio: string;
  dataTermino: string;
  vldTipoEmpreendimento: ETipoEmpreendimento | null;
}
