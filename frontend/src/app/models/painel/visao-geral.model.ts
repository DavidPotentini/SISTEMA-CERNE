export type EEstadoProcesso = 'CONCLUIDO' | 'EM_ANDAMENTO' | 'NAO_INICIADO';

/** Um nó do fluxo de processos: estado derivado das atividades e a contagem x/y. */
export interface ProcessoFluxo {
  prccCod: number;
  nome: string;
  ordem: number;
  estado: EEstadoProcesso;
  atividadesConcluidas: number;
  atividadesTotal: number;
}

/** Resumo de andamento do ciclo em foco (um endpoint, um DTO agregador). */
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
