package com.github.davidpotentini.enums;

/** Espelha o tipo Postgres {@code VLD_ORIGEM_ATIVIDADE}. Persistido como STRING. */
public enum EOrigemAtividade {
    /** Materializada da metodologia ao gerar o ciclo (pode ser ajustada). */
    METODOLOGIA,
    /** Incluída manualmente no planejamento, além das da metodologia. */
    COMPLEMENTAR
}
