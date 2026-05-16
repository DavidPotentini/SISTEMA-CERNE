export enum EBlocoLeanCanvas {
  PROBLEMA = 'PROBLEMA',
  ALTERNATIVAS_EXISTENTE = 'ALTERNATIVAS_EXISTENTE',
  SOLUCAO = 'SOLUCAO',
  PROPOSTA_VALOR = 'PROPOSTA_VALOR',
  METRICA = 'METRICA',
  COMPETENCIA_ESSENCIAL = 'COMPETENCIA_ESSENCIAL',
  CANAIS = 'CANAIS',
  SEGMENTO_CLIENTE = 'SEGMENTO_CLIENTE',
  EARLY_ADOPTERS = 'EARLY_ADOPTERS',
  ESTRUTURA_CUSTOS = 'ESTRUTURA_CUSTOS',
  MODELO_RECEITA = 'MODELO_RECEITA',
}

export const BLOCO_LEAN_LABEL: Record<EBlocoLeanCanvas, string> = {
  [EBlocoLeanCanvas.PROBLEMA]: 'Problema',
  [EBlocoLeanCanvas.ALTERNATIVAS_EXISTENTE]: 'Alternativas Existentes',
  [EBlocoLeanCanvas.SOLUCAO]: 'Solução',
  [EBlocoLeanCanvas.PROPOSTA_VALOR]: 'Proposta de Valor',
  [EBlocoLeanCanvas.METRICA]: 'Métrica',
  [EBlocoLeanCanvas.COMPETENCIA_ESSENCIAL]: 'Competência Essencial',
  [EBlocoLeanCanvas.CANAIS]: 'Canais',
  [EBlocoLeanCanvas.SEGMENTO_CLIENTE]: 'Segmento de Cliente',
  [EBlocoLeanCanvas.EARLY_ADOPTERS]: 'Early Adopters',
  [EBlocoLeanCanvas.ESTRUTURA_CUSTOS]: 'Estrutura de Custos',
  [EBlocoLeanCanvas.MODELO_RECEITA]: 'Modelo de Receita',
};
