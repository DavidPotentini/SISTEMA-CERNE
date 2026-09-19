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
  inicioContrato: string | null;
  fimContrato: string | null;
  cicCod: number | null;
}

export type PessoaRascunho = Pick<PessoaEmpreendimento, 'nome' | 'email' | 'telefone'>;

export type NovoEmpreendimento = Partial<Empreendimento> & { pessoas?: PessoaRascunho[] };

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
