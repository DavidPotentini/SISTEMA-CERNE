export type EStatusCiclo = 'ATIVO' | 'ENCERRADO';

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
