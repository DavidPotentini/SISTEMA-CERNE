import { ESituacaoTarefa } from '../../enums/situacao-tarefa.enum';

export interface TarefaDTO {
  trfCod: number | null;
  nome: string;
  dataInicio: string;
  dataTermino: string;
  eSituacaoTarefa: ESituacaoTarefa;
  objCod: number | null;
  respCod: number | null;
  pontuacao: number | null;
  observacao: string | null;
}
