/** Espelha o tipo Postgres VLD_RECURSO (recursos protegidos por papel). */
export enum ERecurso {
  Metodologia = 'METODOLOGIA',
  Planejamento = 'PLANEJAMENTO',
  AcompanharExecucao = 'ACOMPANHAR_EXECUCAO',
  EvidenciasDocumentos = 'EVIDENCIAS_DOCUMENTOS',
  IndicadoresMetas = 'INDICADORES_METAS',
  ApuracaoIndicadores = 'APURACAO_INDICADORES',
  Empreendimentos = 'EMPREENDIMENTOS',
  Usuarios = 'USUARIOS',
}

/** Espelha o tipo Postgres VLD_NIVEL_PERMISSAO (ordenado do menor para o maior). */
export enum ENivel {
  Nenhum = 'NENHUM',
  Leitura = 'LEITURA',
  Edicao = 'EDICAO',
  Total = 'TOTAL',
}
