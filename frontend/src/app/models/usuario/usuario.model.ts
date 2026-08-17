export type EStatusConta = 'ATIVO' | 'CONVIDADO' | 'SUSPENSO';

/** Linha da listagem "Usuários da plataforma". */
export interface UsuarioResumo {
  ctaCod: number;
  nome: string;
  email: string;
  /** Incubadora vinculada, ou {@code null} ("—"). */
  incCod: number | null;
  incubadora: string | null;
  /** Papel local da incubadora do usuário, ou {@code null} ("—"). */
  papCod: number | null;
  papel: string | null;
  status: EStatusConta;
}

/** Papel disponível na incubadora (opção de atribuição). */
export interface PapelResumo {
  papCod: number;
  nome: string;
}

export const STATUS_CONTA_LABEL: Record<EStatusConta, string> = {
  ATIVO: 'Ativo',
  CONVIDADO: 'Convidado',
  SUSPENSO: 'Suspenso',
};
