export type ETipoPendencia =
  | 'EVIDENCIA_CORRECAO'
  | 'META_VENCIDA'
  | 'ATIVIDADE_ATRASADA'
  | 'ATIVIDADE_ABERTA';

export interface Pendencia {
  tipo: ETipoPendencia;
  /** Código da origem para a navegação (atividade→atpCod, evidência→evdCod, meta→indCod). */
  referenciaId: number;
  titulo: string;
  processoNome: string | null;
  praticaNome: string | null;
  detalhe: string | null;
  prazo: string | null;
  respPesCod: number | null;
}

export const TIPO_PENDENCIA_LABEL: Record<ETipoPendencia, string> = {
  EVIDENCIA_CORRECAO: 'Evidências com correção solicitada',
  META_VENCIDA: 'Metas de indicadores vencidas',
  ATIVIDADE_ATRASADA: 'Atividades atrasadas',
  ATIVIDADE_ABERTA: 'Atividades em aberto'
};

export const TIPO_PENDENCIA_ICONE: Record<ETipoPendencia, string> = {
  EVIDENCIA_CORRECAO: 'edit_note',
  META_VENCIDA: 'event_busy',
  ATIVIDADE_ATRASADA: 'warning',
  ATIVIDADE_ABERTA: 'pending_actions'
};

export const TIPO_PENDENCIA_ROTA: Record<ETipoPendencia, string> = {
  EVIDENCIA_CORRECAO: '/incubadora/evidencias',
  META_VENCIDA: '/incubadora/apuracao',
  ATIVIDADE_ATRASADA: '/incubadora/acompanhamento',
  ATIVIDADE_ABERTA: '/incubadora/acompanhamento',
};

export const TIPOS_PENDENCIA: readonly ETipoPendencia[] = [
  'EVIDENCIA_CORRECAO',
  'META_VENCIDA',
  'ATIVIDADE_ATRASADA',
  'ATIVIDADE_ABERTA',
];
