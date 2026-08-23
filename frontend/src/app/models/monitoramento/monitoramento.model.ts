export type ETipoRodada = 'DIAGNOSTICO_INICIAL' | 'PERIODICO';
export type ESituacaoRodada = 'EM_ANDAMENTO' | 'CONCLUIDA';
export type EStatusMonitoramento = 'EM_ANDAMENTO' | 'CONCLUIDO';
export type ERecomendacaoMonitor =
  | 'CONTINUIDADE'
  | 'REPLANEJAMENTO'
  | 'GRADUACAO'
  | 'DESLIGAMENTO';
export type EEixoCerne = 'EMPREENDEDOR' | 'TECNOLOGIA' | 'CAPITAL' | 'MERCADO' | 'GESTAO';

/** Rodada de monitoramento. {@code empCods} só é usado na escrita (participantes). */
export interface Rodada {
  rodCod: number;
  nome: string;
  tipo: ETipoRodada;
  prazo: string | null;
  respPesCod: number | null;
  responsavelNome: string | null;
  situacao: ESituacaoRodada;
  empCods?: number[];
}

/** Nota (0–5) de um eixo CERNE. */
export interface Pontuacao {
  dimensao: EEixoCerne;
  pontuacao: number | null;
}

/** Aplicação da rodada a um empreendimento — card da aba "Aplicações e pontuação". */
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

/** Uma rodada avaliada de um empreendimento — série do radar de evolução (notas por eixo). */
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

/** Ordem canônica dos eixos CERNE (para exibição e revisão). */
export const EIXOS: readonly EEixoCerne[] = [
  'EMPREENDEDOR',
  'TECNOLOGIA',
  'CAPITAL',
  'MERCADO',
  'GESTAO',
];
