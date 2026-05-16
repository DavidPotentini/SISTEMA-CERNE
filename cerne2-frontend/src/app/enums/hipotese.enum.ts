export enum EStatusHipotese {
  EM_DESENVOLVIMENTO = 'EM_DESENVOLVIMENTO',
  EM_EXECUCAO = 'EM_EXECUCAO',
  CONCLUIDO = 'CONCLUIDO',
}

export enum EResultadoHipotese {
  VALIDADA = 'VALIDADA',
  INVALIDADA = 'INVALIDADA',
}

export enum EDecisaoHipotese {
  PERSEVERAR = 'PERSEVERAR',
  PIVOTAR = 'PIVOTAR',
}

export const STATUS_HIPOTESE_LABEL: Record<EStatusHipotese, string> = {
  [EStatusHipotese.EM_DESENVOLVIMENTO]: 'Em desenvolvimento',
  [EStatusHipotese.EM_EXECUCAO]: 'Em execução',
  [EStatusHipotese.CONCLUIDO]: 'Concluído',
};

export const RESULTADO_HIPOTESE_LABEL: Record<EResultadoHipotese, string> = {
  [EResultadoHipotese.VALIDADA]: 'Validada',
  [EResultadoHipotese.INVALIDADA]: 'Invalidada',
};

export const DECISAO_HIPOTESE_LABEL: Record<EDecisaoHipotese, string> = {
  [EDecisaoHipotese.PERSEVERAR]: 'Perseverar',
  [EDecisaoHipotese.PIVOTAR]: 'Pivotar',
};
