export type EModalidadeFisica = 'RESIDENTE' | 'NAO_RESIDENTE' | 'VIRTUAL';
export type EEstagioEmpreendimento = 'IDEACAO' | 'VALIDACAO' | 'TRACAO' | 'OPERACAO' | 'GRADUACAO';
export type ESituacaoEmpreendimento = 'ATIVO' | 'EM_ANALISE' | 'GRADUADO';
export type EAtivoInativo = 'ATIVO' | 'INATIVO';

/** Empreendimento (startup) incubado. {@code responsavelNome} = responsável interno (equipe). */
export interface Empreendimento {
  empCod: number;
  nome: string;
  setor: string | null;
  modalidadeFisica: EModalidadeFisica | null;
  estagio: EEstagioEmpreendimento | null;
  situacao: ESituacaoEmpreendimento;
  entrada: string | null;
  /** Responsável interno: pessoa da equipe da incubadora (PES_COD). */
  respPesCod: number | null;
  responsavelNome: string | null;
}

/** Pessoa (membro da startup) de um empreendimento. {@code principal} = contato principal. */
export interface PessoaEmpreendimento {
  pseCod: number;
  empCod: number;
  nome: string;
  papel: string | null;
  principal: boolean;
  contato: string | null;
  situacao: EAtivoInativo;
}

/** Candidato a responsável interno: pessoa da equipe da incubadora. */
export interface Responsavel {
  pesCod: number;
  nome: string;
}

export const MODALIDADE_LABEL: Record<EModalidadeFisica, string> = {
  RESIDENTE: 'Residente',
  NAO_RESIDENTE: 'Não residente',
  VIRTUAL: 'Virtual',
};

export const ESTAGIO_LABEL: Record<EEstagioEmpreendimento, string> = {
  IDEACAO: 'Ideação',
  VALIDACAO: 'Validação',
  TRACAO: 'Tração',
  OPERACAO: 'Operação',
  GRADUACAO: 'Graduação',
};

export const SITUACAO_EMP_LABEL: Record<ESituacaoEmpreendimento, string> = {
  ATIVO: 'Ativo',
  EM_ANALISE: 'Em análise',
  GRADUADO: 'Graduado',
};
