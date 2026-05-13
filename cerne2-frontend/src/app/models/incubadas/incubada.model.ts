import { EStatusIncubacao } from '../../enums/status-incubacao.enum';
import { EnderecoRequest, EnderecoResponse } from '../enderecos/endereco.model';

export interface IncubadaRequest {
  nome: string;
  dataInicioIncubacao: string;
  email: string;
  eStatusIncubacao: EStatusIncubacao;
  descricao: string;
  documentacao: string;
  enderecoDTORequest: EnderecoRequest;
}

export interface IncubadaResponse {
  incCod: number;
  nome: string;
  dataInicioIncubacao: string;
  email: string;
  eStatusIncubacao: EStatusIncubacao;
  descricao: string;
  documentacao: string;
  enderecoDTOResponse: EnderecoResponse;
}
