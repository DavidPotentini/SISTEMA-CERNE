export type EStatusPlanejamento = 'PUBLICADO' | 'ENCERRADO';

export const STATUS_PLANEJAMENTO_LABEL: Record<EStatusPlanejamento, string> = {
  PUBLICADO: 'Publicado',
  ENCERRADO: 'Encerrado',
};

export type EOrigemAtividade = 'MODELO' | 'COMPLEMENTAR';

export const ORIGEM_ATIVIDADE_LABEL: Record<EOrigemAtividade, string> = {
  MODELO: 'Do modelo',
  COMPLEMENTAR: 'Complementar',
};

export type EStatusAtividade = 'PLANEJADA' | 'EM_ANDAMENTO' | 'CONCLUIDA' | 'ATRASADA';

export const STATUS_ATIVIDADE_LABEL: Record<EStatusAtividade, string> = {
  PLANEJADA: 'Planejada',
  EM_ANDAMENTO: 'Em andamento',
  CONCLUIDA: 'Concluída',
  ATRASADA: 'Atrasada',
};

/**
 * Planejamento institucional do ciclo ativo. Gerado de um modelo publicado; `inicio`/`fim` vêm do
 * ciclo. `totalAtividades`/`concluidas`/`progresso` (%) alimentam o "Consultar publicação".
 */
export interface Planejamento {
  plnCod: number;
  nome: string;
  cicCod: number;
  cicloNome: string | null;
  modCod: number | null;
  modeloNome: string | null;
  status: EStatusPlanejamento;
  inicio: string | null;
  fim: string | null;
  respPesCod: number | null;
  responsavel: string | null;
  totalAtividades: number;
  concluidas: number;
  progresso: number;
}

/** Situação do ciclo ativo: se há ciclo e o planejamento vigente (ou `null` se ainda não gerado). */
export interface PlanejamentoAtual {
  cicloAtivo: boolean;
  cicCod: number | null;
  cicloNome: string | null;
  planejamento: Planejamento | null;
}

/** Atividade planejada de uma prática (ajustável; complementares podem ser incluídas/removidas). */
export interface AtividadePlanejada {
  atpCod: number;
  plnCod: number;
  origem: EOrigemAtividade;
  prtCod: number;
  nome: string;
  descricao: string | null;
  respPesCod: number | null;
  prazo: string | null;
  status: EStatusAtividade;
  empCod: number | null;
}

/** Prática dentro da estrutura do planejamento — só leitura (da metodologia) + atividades. */
export interface PlanPratica {
  prtCod: number;
  nome: string;
  descricao: string | null;
  atividades: AtividadePlanejada[];
}

/** Processo dentro da estrutura do planejamento — só leitura, ordenado por `ordem`. */
export interface PlanProcesso {
  prcCod: number;
  ordem: number;
  nome: string;
  descricao: string | null;
  praticas: PlanPratica[];
}
