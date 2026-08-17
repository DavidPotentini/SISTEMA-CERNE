package com.github.davidpotentini.enums;

/**
 * Espelha o tipo Postgres {@code VLD_STATUS_ATIVIDADE}. Persistido como STRING. Estado de execução
 * da atividade planejada; o progresso do planejamento é a fração em {@code CONCLUIDA}.
 */
public enum EStatusAtividade {
    PLANEJADA,
    EM_ANDAMENTO,
    CONCLUIDA,
    ATRASADA
}
