export type EAtivoInativo = 'ATIVO' | 'INATIVO';
export type ESituacaoVersao = 'RASCUNHO' | 'VIGENTE' | 'HISTORICA';

/**
 * Versão da metodologia. As abas editam o RASCUNHO (versão de trabalho) — usam seu `verCod` nas
 * operações; `alterada` indica mudanças não publicadas. No histórico, `publicadoPor` é o nome de
 * quem publicou.
 */
export interface Versao {
  verCod: number;
  versao: string;
  situacao: ESituacaoVersao;
  alterada: boolean;
  publicadaEm: string | null;
  pubPesCod: number | null;
  publicadoPor: string | null;
  resumo: string | null;
}

/** Prática de um processo (aparece dentro do accordion do processo). */
export interface Pratica {
  prtCod: number;
  prcCod: number;
  nome: string;
  descricao: string | null;
  situacao: EAtivoInativo;
}

/** Processo da metodologia (um accordion). Ordenado por `ordem`; traz suas práticas. */
export interface Processo {
  prcCod: number;
  verCod: number;
  ordem: number;
  nome: string;
  descricao: string | null;
  situacao: EAtivoInativo;
  praticas: Pratica[];
}

export type EPeriodicidade =
  | 'MENSAL'
  | 'BIMESTRAL'
  | 'TRIMESTRAL'
  | 'SEMESTRAL'
  | 'ANUAL'
  | 'POR_CICLO'
  | 'NAO_SE_APLICA';

export const PERIODICIDADE_LABEL: Record<EPeriodicidade, string> = {
  MENSAL: 'Mensal',
  BIMESTRAL: 'Bimestral',
  TRIMESTRAL: 'Trimestral',
  SEMESTRAL: 'Semestral',
  ANUAL: 'Anual',
  POR_CICLO: 'Por ciclo',
  NAO_SE_APLICA: 'Não se aplica',
};

/**
 * Indicador da metodologia. Vincula-se a uma prática pelo `prtCod` ("Vínculo metodológico");
 * `vinculoMetodologico` é o nome da prática (só leitura).
 */
export interface Indicador {
  inmCod: number;
  prtCod: number;
  vinculoMetodologico: string | null;
  nome: string;
  unidade: string | null;
  periodicidade: EPeriodicidade;
  situacao: EAtivoInativo;
}
