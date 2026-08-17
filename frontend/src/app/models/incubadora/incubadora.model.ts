export type EStatusIncubadora = 'EM_OPERACAO' | 'AGUARDANDO_ATIVACAO' | 'SUSPENSA';
export type ENivelIncubadora = 'CERNE_1';

/** Linha da listagem (colunas: incubadora, mantenedora, responsável, usuários, status). */
export interface IncubadoraResumo {
  incCod: number;
  nome: string;
  mantenedora: string | null;
  responsavelNome: string | null;
  qtdUsuarios: number;
  status: EStatusIncubadora;
}

/** Detalhe completo (Consultar / Configurar). */
export interface IncubadoraDetalhe {
  incCod: number;
  nome: string;
  cnpj: string | null;
  mantenedora: string | null;
  respCtaCod: number | null;
  responsavelNome: string | null;
  email: string | null;
  telefone: string | null;
  cidade: string | null;
  nivel: ENivelIncubadora | null;
  status: EStatusIncubadora;
  nomeSchema: string | null;
  criadaEm: string | null;
  ativadaEm: string | null;
}

export const STATUS_LABEL: Record<EStatusIncubadora, string> = {
  EM_OPERACAO: 'Em operação',
  AGUARDANDO_ATIVACAO: 'Aguardando ativação',
  SUSPENSA: 'Suspensa',
};

export const NIVEL_LABEL: Record<ENivelIncubadora, string> = {
  CERNE_1: 'CERNE 1',
};
