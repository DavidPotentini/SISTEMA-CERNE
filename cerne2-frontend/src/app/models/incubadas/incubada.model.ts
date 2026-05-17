import { EStatusIncubacao } from '../../enums/status-incubacao.enum';
import { EnderecoDTO } from '../enderecos/endereco.model';

export interface IncubadaDTO {
  incCod: number | null;
  nome: string;
  dataInicioIncubacao: string;
  email: string;
  eStatusIncubacao: EStatusIncubacao;
  descricao: string;
  documentacao: string;
  enderecoDTO: EnderecoDTO;
}
