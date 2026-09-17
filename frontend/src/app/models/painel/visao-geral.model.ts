export type EEstadoProcesso = 'CONCLUIDO' | 'EM_ANDAMENTO' | 'NAO_INICIADO';

export interface ProcessoFluxo {
  prccCod: number;
  nome: string;
  ordem: number;
  estado: EEstadoProcesso;
  atividadesConcluidas: number;
  atividadesTotal: number;
}

export interface ResumoCiclo {
  cicloNome: string | null;
  atividadesConcluidas: number;
  atividadesTotal: number;
  progresso: number;
  empreendimentosAtivos: number;
  evidenciasRegistradas: number;
  evidenciasValidadas: number;
  indicadoresAtingidos: number;
  indicadoresComMeta: number;
  fluxo: ProcessoFluxo[];
}

export const ESTADO_PROCESSO_LABEL: Record<EEstadoProcesso, string> = {
  CONCLUIDO: 'Concluído',
  EM_ANDAMENTO: 'Em andamento',
  NAO_INICIADO: 'Não iniciado',
};
