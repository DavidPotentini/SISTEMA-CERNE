package com.github.davidpotentini.enums;

/** Espelha o tipo Postgres {@code VLD_STATUS_PLANEJAMENTO}. Persistido como STRING. */
public enum EStatusPlanejamento {
    /** Planejamento do ciclo ativo — atividades podem ser ajustadas/incluídas. */
    PUBLICADO,
    /** Ciclo encerrado — só consulta. */
    ENCERRADO
}
