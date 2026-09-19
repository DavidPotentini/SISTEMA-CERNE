export type EAtivoInativo = 'ATIVO' | 'INATIVO';

export type ENivelCerne = 'CERNE_1';

export const NIVEL_CERNE_LABEL: Record<ENivelCerne, string> = {
  CERNE_1: 'CERNE 1',
};

export interface Pratica {
  prtCod: number;
  prcCod: number;
  nome: string;
  descricao: string | null;
  situacao: EAtivoInativo;
}

export interface Processo {
  prcCod: number;
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

export interface Indicador {
  inmCod: number;
  prtCod: number;
  processoNome: string | null;
  praticaNome: string | null;
  nome: string;
  unidade: string | null;
  periodicidade: EPeriodicidade;
  situacao: EAtivoInativo;
}

export interface Agrupamento {
  agrCod: number;
  prtCod: number;
  vinculoMetodologico: string | null;
  nome: string;
  descricao: string | null;
  ordem: number;
  situacao: EAtivoInativo;
}

export interface AtividadeMetodologia {
  ameCod: number;
  prtCod: number;
  agrCod: number | null;
  vinculoMetodologico: string | null;
  nome: string;
  observacoes: string | null;
  porEmpreendimento: boolean;
  situacao: EAtivoInativo;
}

export interface EmpreendimentoOpcao {
  empCod: number;
  nome: string;
}
export interface GerarCicloOpcoes {
  incubadas: EmpreendimentoOpcao[];
  selecionadas: number[];
}
