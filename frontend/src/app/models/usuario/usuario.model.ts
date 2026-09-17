export type EStatusConta = 'ATIVO' | 'CONVIDADO' | 'SUSPENSO';

export interface UsuarioResumo {
  ctaCod: number;
  nome: string;
  email: string;
  incCod: number | null;
  incubadora: string | null;
  papCod: number | null;
  papel: string | null;
  status: EStatusConta;
}

export interface PapelResumo {
  papCod: number;
  nome: string;
}

export const STATUS_CONTA_LABEL: Record<EStatusConta, string> = {
  ATIVO: 'Ativo',
  CONVIDADO: 'Convidado',
  SUSPENSO: 'Suspenso',
};
