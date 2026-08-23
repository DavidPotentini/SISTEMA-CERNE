export type ETipoPendencia =
  | 'ATIVIDADE_ABERTA'
  | 'ATIVIDADE_ATRASADA'
  | 'EVIDENCIA_CORRECAO'
  | 'META_VENCIDA';

/** Uma pendência do Painel Operacional (lista unificada; o front agrupa por {@code tipo}). */
export interface Pendencia {
  tipo: ETipoPendencia;
  /** Código da origem para a navegação (atividade→atpCod, evidência→evdCod, meta→indCod). */
  referenciaId: number;
  titulo: string;
  /** Processo/prática CERNE a que o item pertence (situa a pendência na estrutura). */
  processoNome: string | null;
  praticaNome: string | null;
  detalhe: string | null;
  prazo: string | null;
  /** Responsável da origem (atividade/evidência→quem registrou/meta→responsável do indicador). */
  respPesCod: number | null;
}

export const TIPO_PENDENCIA_LABEL: Record<ETipoPendencia, string> = {
  ATIVIDADE_ABERTA: 'Atividades em aberto',
  ATIVIDADE_ATRASADA: 'Atividades atrasadas',
  EVIDENCIA_CORRECAO: 'Evidências com correção solicitada',
  META_VENCIDA: 'Metas de indicadores vencidas',
};

/** Ícone Material por tipo. */
export const TIPO_PENDENCIA_ICONE: Record<ETipoPendencia, string> = {
  ATIVIDADE_ABERTA: 'pending_actions',
  ATIVIDADE_ATRASADA: 'warning',
  EVIDENCIA_CORRECAO: 'edit_note',
  META_VENCIDA: 'event_busy',
};

/** Rota de origem por tipo (para onde o item navega). */
export const TIPO_PENDENCIA_ROTA: Record<ETipoPendencia, string> = {
  ATIVIDADE_ABERTA: '/incubadora/acompanhamento',
  ATIVIDADE_ATRASADA: '/incubadora/acompanhamento',
  EVIDENCIA_CORRECAO: '/incubadora/evidencias',
  META_VENCIDA: '/incubadora/apuracao',
};

/** Ordem de exibição das seções (mais urgente primeiro). */
export const TIPOS_PENDENCIA: readonly ETipoPendencia[] = [
  'ATIVIDADE_ATRASADA',
  'ATIVIDADE_ABERTA',
  'EVIDENCIA_CORRECAO',
  'META_VENCIDA',
];
