export type ETipoRodada = 'DIAGNOSTICO_INICIAL' | 'PERIODICO';
export type ESituacaoRodada = 'EM_ANDAMENTO' | 'CONCLUIDA';
export type EStatusMonitoramento = 'EM_ANDAMENTO' | 'CONCLUIDO';
export type ERecomendacaoMonitor =
  | 'CONTINUIDADE'
  | 'REPLANEJAMENTO'
  | 'GRADUACAO'
  | 'DESLIGAMENTO';
export type EEixoCerne = 'EMPREENDEDOR' | 'TECNOLOGIA' | 'CAPITAL' | 'MERCADO' | 'GESTAO';

export interface Rodada {
  rodCod: number;
  nome: string;
  tipo: ETipoRodada;
  prazo: string | null;
  respPesCod: number | null;
  responsavelNome: string | null;
  situacao: ESituacaoRodada;
  /** Só usado na escrita (participantes). */
  empCods?: number[];
}

export interface Pontuacao {
  dimensao: EEixoCerne;
  pontuacao: number | null;
}

export interface Aplicacao {
  avaCod: number | null;
  empCod: number;
  empNome: string | null;
  status: EStatusMonitoramento | null;
  data: string | null;
  recomendacao: ERecomendacaoMonitor | null;
  observacao: string | null;
  pontuacoes: Pontuacao[];
}

export interface EvolucaoRodada {
  rodCod: number;
  rodadaNome: string | null;
  data: string | null;
  pontuacoes: Pontuacao[];
}

export const TIPO_RODADA_LABEL: Record<ETipoRodada, string> = {
  DIAGNOSTICO_INICIAL: 'Diagnóstico inicial',
  PERIODICO: 'Periódico',
};

export const SITUACAO_RODADA_LABEL: Record<ESituacaoRodada, string> = {
  EM_ANDAMENTO: 'Em andamento',
  CONCLUIDA: 'Concluída',
};

export const STATUS_MONITORAMENTO_LABEL: Record<EStatusMonitoramento, string> = {
  EM_ANDAMENTO: 'Em andamento',
  CONCLUIDO: 'Concluído',
};

export const RECOMENDACAO_LABEL: Record<ERecomendacaoMonitor, string> = {
  CONTINUIDADE: 'Continuidade',
  REPLANEJAMENTO: 'Replanejamento',
  GRADUACAO: 'Graduação',
  DESLIGAMENTO: 'Desligamento',
};

export const EIXO_LABEL: Record<EEixoCerne, string> = {
  EMPREENDEDOR: 'Empreendedor',
  TECNOLOGIA: 'Tecnologia',
  CAPITAL: 'Capital',
  MERCADO: 'Mercado',
  GESTAO: 'Gestão',
};

export const EIXOS: readonly EEixoCerne[] = [
  'EMPREENDEDOR',
  'TECNOLOGIA',
  'CAPITAL',
  'MERCADO',
  'GESTAO',
];
