import { EAtivoInativo, EPeriodicidade } from '../metodologia/metodologia.model';

export type EOrigemIndicador = 'METODOLOGIA_CERNE' | 'COMPLEMENTAR';

export const ORIGEM_INDICADOR_LABEL: Record<EOrigemIndicador, string> = {
  METODOLOGIA_CERNE: 'Metodologia CERNE',
  COMPLEMENTAR: 'Complementar',
};

/**
 * Indicador de um ciclo. Origem `METODOLOGIA_CERNE` (gerado da metodologia vigente) ou `COMPLEMENTAR`
 * (definido à mão). O "Vínculo CERNE" é o par processo/prática (só leitura), derivado do `prtCod`. O
 * `respPesCod` (responsável pela apuração) é o único campo editável dos gerados; `responsavelNome` é
 * o rótulo resolvido para exibição.
 */
export interface IndicadorCiclo {
  indCod: number;
  nome: string;
  origem: EOrigemIndicador;
  prtCod: number | null;
  unidade: string | null;
  periodicidade: EPeriodicidade;
  situacao: EAtivoInativo;
  respPesCod: number | null;
  processoNome: string | null;
  praticaNome: string | null;
  responsavelNome: string | null;
}

/** Opção de vínculo CERNE (prática da metodologia vigente) para o cadastro complementar. */
export interface PraticaOpcao {
  prtCod: number;
  praticaNome: string | null;
  prcCod: number;
  processoNome: string | null;
}

/**
 * Meta de um indicador para um período. `valor` é a meta estipulada; `dataInicioApuracao`/
 * `dataFimApuracao` (ISO `yyyy-MM-dd`) delimitam a janela de apuração.
 */
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

/**
 * Linha da tela de apuração: o indicador do ciclo + o resumo `apurados`/`totalPeriodos` e a `situacao`
 * (em aberto/atrasada/concluída, derivada dos períodos). Sem períodos (`totalPeriodos === 0`), a UI
 * mostra "sem meta" e `situacao` fica `null`.
 */
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

/**
 * Linha do painel do ciclo: o indicador com meta e resultado somados de todos os períodos.
 * `atingido` = resultado somado ≥ meta somada (com meta > 0); `pendente` = há período já encerrado
 * (fim < hoje) sem resultado. Cards e consolidação por processo são derivados desta lista.
 */
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

/**
 * Período na apuração: a meta estipulada + a janela e, se apurado, o resultado (`resultadoValor`) com
 * quem registrou (`registradoPor`) e quando (`dataRegistro`). Campos de resultado ficam `null` até
 * apurar.
 */
export interface PeriodoApuracao {
  metCod: number;
  metaValor: number | null;
  dataInicioApuracao: string | null;
  dataFimApuracao: string | null;
  resultadoValor: number | null;
  registradoPor: string | null;
  dataRegistro: string | null;
}
