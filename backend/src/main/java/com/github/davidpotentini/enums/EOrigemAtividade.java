package com.github.davidpotentini.enums;

/** Espelha o tipo Postgres {@code VLD_ORIGEM_ATIVIDADE}. Persistido como STRING. */
public enum EOrigemAtividade {
    /** Copiada do modelo ao gerar o planejamento (pode ser ajustada). */
    MODELO,
    /** Incluída manualmente no planejamento, além das do modelo. */
    COMPLEMENTAR
}
