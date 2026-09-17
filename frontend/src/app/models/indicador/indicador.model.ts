import { EAtivoInativo, EPeriodicidade } from '../metodologia/metodologia.model';

export type EOrigemIndicador = 'METODOLOGIA_CERNE' | 'COMPLEMENTAR';

export const ORIGEM_INDICADOR_LABEL: Record<EOrigemIndicador, string> = {
  METODOLOGIA_CERNE: 'Metodologia CERNE',
  COMPLEMENTAR: 'Complementar',
};

export interface IndicadorCiclo {
  indCod: number;
  nome: string;
  origem: EOrigemIndicador;
  prtcCod: number | null;
  unidade: string | null;
  periodicidade: EPeriodicidade;
  situacao: EAtivoInativo;
  respPesCod: number | null;
  processoNome: string | null;
  praticaNome: string | null;
  responsavelNome: string | null;
}

export interface PraticaOpcao {
  prtcCod: number;
  praticaNome: string | null;
  prccCod: number;
  processoNome: string | null;
}

export interface Meta {
  metCod: number;
  valor: number | null;
  dataInicioApuracao: string | null;
  dataFimApuracao: string | null;
}

export type ESituacaoApuracao = 'EM_ABERTO' | 'ATRASADA' | 'CONCLUIDA';

export const SITUACAO_APURACAO_LABEL: Record<ESituacaoApuracao, string> = {
  EM_ABERTO: 'Em aberto',
  ATRASADA: 'Atrasada',
  CONCLUIDA: 'Concluída',
};

export interface ApuracaoIndicador {
  indCod: number;
  nome: string;
  processoNome: string | null;
  praticaNome: string | null;
  periodicidade: EPeriodicidade;
  unidade: string | null;
  totalPeriodos: number;
  apurados: number;
  situacao: ESituacaoApuracao | null;
  respPesCod: number | null;
}

export interface PainelIndicador {
  indCod: number;
  nome: string;
  processoNome: string | null;
  praticaNome: string | null;
  periodicidade: EPeriodicidade;
  unidade: string | null;
  temMeta: boolean;
  metaTotal: number;
  atingidoTotal: number;
  atingido: boolean;
  pendente: boolean;
}

export interface PeriodoApuracao {
  metCod: number;
  metaValor: number | null;
  dataInicioApuracao: string | null;
  dataFimApuracao: string | null;
  resultadoValor: number | null;
  registradoPor: string | null;
  dataRegistro: string | null;
}
