export type EStatusCiclo = 'ATIVO' | 'ENCERRADO';

/**
 * Ciclo da incubadora. {@code status} (ATIVO = edita / ENCERRADO = só consulta) e {@code emFoco}
 * (o ciclo refletido nas telas) são independentes; no máx. um ativo e um em foco por incubadora.
 */
export interface Ciclo {
  cicCod: number;
  nome: string;
  inicio: string | null;
  fim: string | null;
  status: EStatusCiclo;
  emFoco: boolean;
}

export const STATUS_CICLO_LABEL: Record<EStatusCiclo, string> = {
  ATIVO: 'Ativo',
  ENCERRADO: 'Encerrado',
};
