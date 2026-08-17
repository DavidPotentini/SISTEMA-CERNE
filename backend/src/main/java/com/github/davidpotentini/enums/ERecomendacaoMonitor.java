package com.github.davidpotentini.enums;

/** Recomendação do monitor ao avaliar uma incubada. Espelha o tipo Postgres {@code VLD_RECOMENDACAO_MONITOR}. Persistido como STRING. */
public enum ERecomendacaoMonitor {
    CONTINUIDADE,
    REPLANEJAMENTO,
    GRADUACAO,
    DESLIGAMENTO
}
