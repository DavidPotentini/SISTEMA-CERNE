export type EStatusEvidencia = 'PENDENTE_VALIDACAO' | 'VALIDADA' | 'CORRECAO_SOLICITADA';

export const STATUS_EVIDENCIA_LABEL: Record<EStatusEvidencia, string> = {
  PENDENTE_VALIDACAO: 'Pendente validação',
  VALIDADA: 'Validada',
  CORRECAO_SOLICITADA: 'Correção solicitada',
};

export interface AtividadeOpcao {
  atpCod: number;
  nome: string;
  prtcCod: number;
  praticaNome: string | null;
  prccCod: number;
  processoNome: string | null;
}

export interface Arquivo {
  arqCod: number;
  nomeOriginal: string | null;
  contentType: string | null;
  tamanhoBytes: number | null;
  url: string | null;
}

export interface Evidencia {
  evdCod: number;
  evdCodSeq: number;
  titulo: string;
  atpCod: number;
  atividadeNome: string | null;
  processoNome: string | null;
  praticaNome: string | null;
  arqCod: number | null;
  arquivoNome: string | null;
  status: EStatusEvidencia;
  regPesCod: number | null;
  responsavel: string | null;
  data: string | null;
  motivoCorrecao: string | null;
}
