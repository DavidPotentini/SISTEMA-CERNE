import { EAtivoInativo, EPeriodicidade } from '../metodologia/metodologia.model';

export type EStatusModelo = 'RASCUNHO' | 'PUBLICADO';

export const STATUS_MODELO_LABEL: Record<EStatusModelo, string> = {
  RASCUNHO: 'Rascunho',
  PUBLICADO: 'Publicado',
};

/**
 * Modelo de planejamento. Herda a estrutura da metodologia da incubadora (lida ao vivo). Enquanto
 * `RASCUNHO` as atividades são editáveis; `PUBLICADO` é imutável.
 */
export interface Modelo {
  modCod: number;
  nome: string;
  periodicidade: EPeriodicidade;
  status: EStatusModelo;
  publicadoEm: string | null;
  descricao: string | null;
}

/** Atividade de uma prática do modelo (único nível editável na tela). */
export interface AtividadeModelo {
  atmCod: number;
  modCod: number;
  prtCod: number;
  nome: string;
  observacoes: string | null;
  respPesCod: number | null;
  situacao: EAtivoInativo;
}

/** Prática dentro da estrutura do modelo — só leitura (da metodologia) + atividades. */
export interface ModeloPratica {
  prtCod: number;
  nome: string;
  descricao: string | null;
  atividades: AtividadeModelo[];
}

/** Processo dentro da estrutura do modelo — só leitura, ordenado por `ordem`. */
export interface ModeloProcesso {
  prcCod: number;
  ordem: number;
  nome: string;
  descricao: string | null;
  praticas: ModeloPratica[];
}
