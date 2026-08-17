package com.github.davidpotentini.enums;

/** Espelha o tipo Postgres {@code VLD_ORIGEM_INDICADOR}. Persistido como STRING. */
public enum EOrigemIndicador {
    /** Copiado da metodologia vigente ao gerar os indicadores do ciclo. */
    METODOLOGIA_CERNE,
    /** Definido manualmente no ciclo, além dos da metodologia. */
    COMPLEMENTAR
}
