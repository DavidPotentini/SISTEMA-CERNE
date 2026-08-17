export type EStatusEvidencia = 'EM_VALIDACAO' | 'VALIDADA' | 'CORRECAO_SOLICITADA';

export const STATUS_EVIDENCIA_LABEL: Record<EStatusEvidencia, string> = {
  EM_VALIDACAO: 'Em validação',
  VALIDADA: 'Validada',
  CORRECAO_SOLICITADA: 'Correção solicitada',
};

/**
 * Opção de atividade para o cadastro de evidência (lista plana do plano vigente). A UI agrupa por
 * `prcCod`/`prtCod` para montar os dropdowns em cascata processo → prática → atividade.
 */
export interface AtividadeOpcao {
  atpCod: number;
  nome: string;
  prtCod: number;
  praticaNome: string | null;
  prcCod: number;
  processoNome: string | null;
}

/** Metadados de um arquivo enviado; `url` é a URL de download temporária (presigned). */
export interface Arquivo {
  arqCod: number;
  nomeOriginal: string | null;
  contentType: string | null;
  tamanhoBytes: number | null;
  url: string | null;
}

/**
 * Uma versão da evidência. Na listagem, é a versão corrente de cada evidência; no histórico (ABRIR),
 * cada item é uma versão. Ao registrar/corrigir, envia-se `titulo`, `atpCod` e `arqCod`. O
 * `motivoCorrecao` é o motivo da rejeição — presente só quando `status` é `CORRECAO_SOLICITADA`.
 */
export interface Evidencia {
  evdCod: number;
  evdCodSeq: number;
  titulo: string;
  atpCod: number;
  atividadeNome: string | null;
  arqCod: number | null;
  arquivoNome: string | null;
  status: EStatusEvidencia;
  regPesCod: number | null;
  responsavel: string | null;
  data: string | null;
  motivoCorrecao: string | null;
}
