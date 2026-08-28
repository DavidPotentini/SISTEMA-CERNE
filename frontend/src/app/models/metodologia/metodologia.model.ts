export type EAtivoInativo = 'ATIVO' | 'INATIVO';

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

/**
 * Atividade-padrão da metodologia. Vincula-se a uma prática pelo `prtCod` ("Vínculo metodológico");
 * `vinculoMetodologico` é o nome da prática (só leitura). "Quem"/"quando" só no planejamento.
 */
export interface AtividadeMetodologia {
  ameCod: number;
  prtCod: number;
  vinculoMetodologico: string | null;
  nome: string;
  observacoes: string | null;
  situacao: EAtivoInativo;
}
