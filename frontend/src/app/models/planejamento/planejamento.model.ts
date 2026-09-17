export type EStatusPlanejamento = 'PUBLICADO' | 'ENCERRADO';

export const STATUS_PLANEJAMENTO_LABEL: Record<EStatusPlanejamento, string> = {
  PUBLICADO: 'Publicado',
  ENCERRADO: 'Encerrado',
};

export type EOrigemAtividade = 'METODOLOGIA' | 'COMPLEMENTAR';

export const ORIGEM_ATIVIDADE_LABEL: Record<EOrigemAtividade, string> = {
  METODOLOGIA: 'Da metodologia',
  COMPLEMENTAR: 'Complementar',
};

export type EStatusAtividade = 'PLANEJADA' | 'EM_ANDAMENTO' | 'CONCLUIDA' | 'ATRASADA';

export const STATUS_ATIVIDADE_LABEL: Record<EStatusAtividade, string> = {
  PLANEJADA: 'Planejada',
  EM_ANDAMENTO: 'Em andamento',
  CONCLUIDA: 'Concluída',
  ATRASADA: 'Atrasada',
};

export interface Planejamento {
  plnCod: number;
  nome: string;
  cicCod: number;
  cicloNome: string | null;
  status: EStatusPlanejamento;
  inicio: string | null;
  fim: string | null;
  respPesCod: number | null;
  responsavel: string | null;
  totalAtividades: number;
  concluidas: number;
  progresso: number;
}

export interface PlanejamentoAtual {
  cicloAtivo: boolean;
  cicCod: number | null;
  cicloNome: string | null;
  planejamento: Planejamento | null;
}

export interface AtividadePlanejada {
  atpCod: number;
  plnCod: number;
  origem: EOrigemAtividade;
  prtcCod: number;
  agrcCod: number | null;
  nome: string;
  observacoes: string | null;
  respPesCod: number | null;
  prazo: string | null;
  status: EStatusAtividade;
  empCod: number | null;
  empreendimentoNome: string | null;
  responsavelNome: string | null;
}

export interface PlanGrupo {
  agrcCod: number | null;
  nome: string;
  ordem: number;
  /** Empreendimento do grupo dinâmico "da incubada" (nulo nos grupos do template / "Sem agrupamento"). */
  empCod: number | null;
  atividades: AtividadePlanejada[];
}

export interface PlanPratica {
  prtcCod: number;
  nome: string;
  descricao: string | null;
  grupos: PlanGrupo[];
}

export type ENivelCerne = 'CERNE_1';

export const NIVEL_CERNE_LABEL: Record<ENivelCerne, string> = {
  CERNE_1: 'CERNE 1',
};

export interface PlanProcesso {
  prccCod: number;
  nivel: ENivelCerne;
  ordem: number;
  nome: string;
  descricao: string | null;
  praticas: PlanPratica[];
}
