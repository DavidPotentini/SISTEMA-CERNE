package com.github.davidpotentini.enums;

/** Tipo da rodada de monitoramento. Espelha o tipo Postgres {@code VLD_TIPO_RODADA}. Persistido como STRING. */
public enum ETipoRodada {
    /** Diagnóstico inicial da incubada (entrada). */
    DIAGNOSTICO_INICIAL,
    /** Acompanhamento periódico. */
    PERIODICO
}
