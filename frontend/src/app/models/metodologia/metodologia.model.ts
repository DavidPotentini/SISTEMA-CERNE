export type EAtivoInativo = 'ATIVO' | 'INATIVO';

/** Nível CERNE de um processo. Por ora o sistema trata só o Nível I. */
export type ENivelCerne = 'CERNE_1';

export const NIVEL_CERNE_LABEL: Record<ENivelCerne, string> = {
  CERNE_1: 'CERNE 1',
};

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
  /** Nível CERNE do processo — por ora sempre 'CERNE_1'. */
  nivel: ENivelCerne;
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
 * Agrupamento (sub-plano do Manual) de uma prática — nível entre prática e atividade. Vincula-se a uma
 * prática pelo `prtCod`; `vinculoMetodologico` é o nome dela (só leitura). `ordem` é gerida por arrastar.
 */
export interface Agrupamento {
  agrCod: number;
  prtCod: number;
  vinculoMetodologico: string | null;
  nome: string;
  descricao: string | null;
  ordem: number;
  situacao: EAtivoInativo;
}

/**
 * Atividade-padrão da metodologia. Vincula-se a uma prática pelo `prtCod` ("Vínculo metodológico");
 * `vinculoMetodologico` é o nome da prática (só leitura). `agrCod` é o sub-plano (opcional) dentro da
 * prática. "Quem"/"quando" só no planejamento.
 */
export interface AtividadeMetodologia {
  ameCod: number;
  prtCod: number;
  agrCod: number | null;
  vinculoMetodologico: string | null;
  nome: string;
  observacoes: string | null;
  /** Atividade que repete por empreendimento na geração do ciclo (uma cópia por incubada). */
  porEmpreendimento: boolean;
  situacao: EAtivoInativo;
}

/** Opção de incubada e seleção atual do ciclo, para o diálogo "Gerar do ciclo". */
export interface EmpreendimentoOpcao {
  empCod: number;
  nome: string;
}
export interface GerarCicloOpcoes {
  incubadas: EmpreendimentoOpcao[];
  selecionadas: number[];
}
