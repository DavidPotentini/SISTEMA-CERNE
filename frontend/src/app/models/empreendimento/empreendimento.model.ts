export type EEstagioIncubacao = 'IDEACAO' | 'PRE_INCUBACAO' | 'INCUBACAO' | 'POS_INCUBACAO';
export type EStatusEmpreendimento = 'ATIVO' | 'DESLIGADO' | 'GRADUADO';
export type ESituacaoContrato =
  | 'ASSINADO'
  | 'PENDENTE'
  | 'DISTRATO'
  | 'NAO_ASSINADO'
  | 'ENVIADO'
  | 'IRREGULAR'
  | 'FINALIZADO';
export type ENivelMaturidade = 'IDEACAO' | 'VALIDACAO' | 'OPERACAO' | 'TRACAO' | 'ESCALA';

/** Empreendimento (startup) incubado. O vínculo com o ciclo é feito à parte (CICLO_EMPREENDIMENTOS). */
export interface Empreendimento {
  empCod: number;
  nome: string;
  cnpj: string | null;
  cnae: string | null;
  atividadeEconomica: string | null;
  instagram: string | null;
  site: string | null;
  email: string | null;
  situacaoContrato: ESituacaoContrato | null;
  estagio: EEstagioIncubacao | null;
  status: EStatusEmpreendimento;
  nivelMaturidade: ENivelMaturidade | null;
  entrada: string | null;
  saida: string | null;
}

/** Pessoa em rascunho (na criação do empreendimento, antes de existir chave). */
export type PessoaRascunho = Pick<PessoaEmpreendimento, 'nome' | 'email' | 'telefone'>;

/** Payload de criação: dados do empreendimento + pessoas iniciais opcionais (gravadas junto). */
export type NovoEmpreendimento = Partial<Empreendimento> & { pessoas?: PessoaRascunho[] };

/** Pessoa (membro da startup) de um empreendimento. {@code representanteLegal} = representante legal. */
export interface PessoaEmpreendimento {
  pseCod: number;
  empCod: number;
  nome: string;
  representanteLegal: boolean;
  email: string | null;
  telefone: string | null;
}

export const ESTAGIO_LABEL: Record<EEstagioIncubacao, string> = {
  IDEACAO: 'Ideação',
  PRE_INCUBACAO: 'Pré-incubação',
  INCUBACAO: 'Incubação',
  POS_INCUBACAO: 'Pós-incubação',
};

export const STATUS_EMP_LABEL: Record<EStatusEmpreendimento, string> = {
  ATIVO: 'Ativo',
  DESLIGADO: 'Desligado',
  GRADUADO: 'Graduado',
};

export const SITUACAO_CONTRATO_LABEL: Record<ESituacaoContrato, string> = {
  ASSINADO: 'Assinado',
  PENDENTE: 'Pendente',
  DISTRATO: 'Distrato',
  NAO_ASSINADO: 'Não assinado',
  ENVIADO: 'Enviado',
  IRREGULAR: 'Irregular',
  FINALIZADO: 'Finalizado',
};

export const NIVEL_MATURIDADE_LABEL: Record<ENivelMaturidade, string> = {
  IDEACAO: 'Ideação',
  VALIDACAO: 'Validação',
  OPERACAO: 'Operação',
  TRACAO: 'Tração',
  ESCALA: 'Escala',
};
