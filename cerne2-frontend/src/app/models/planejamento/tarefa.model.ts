import { ESituacaoTarefa } from '../../enums/situacao-tarefa.enum';

export interface TarefaResponse {
  trfCod:          number;
  nome:            string;
  dataInicio:      string;
  dataTermino:     string;
  eSituacaoTarefa: ESituacaoTarefa;
  objCod:          number;
  respCod:         number;
}

export interface TarefaRequest {
  nome:            string;
  dataInicio:      string;
  dataTermino:     string;
  eSituacaoTarefa: ESituacaoTarefa;
  respCod:         number;
}