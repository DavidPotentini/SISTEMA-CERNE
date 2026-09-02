package com.github.davidpotentini.enums;

/**
 * Tipo de pendência (tela Pendências). Enum <b>só de API</b> (não espelha tipo do banco):
 * discrimina as linhas da lista unificada de pendências para o front agrupar em seções e contar.
 */
public enum ETipoPendencia {
    ATIVIDADE_ABERTA,
    ATIVIDADE_ATRASADA,
    EVIDENCIA_CORRECAO,
    META_VENCIDA
}
