package com.github.davidpotentini.enums;

/** Espelha o tipo Postgres {@code VLD_STATUS_EVIDENCIA}. Persistido como STRING. */
public enum EStatusEvidencia {
    /** Registrada/reenviada, aguardando avaliação. */
    PENDENTE_VALIDACAO,
    /** Aprovada pelo avaliador. */
    VALIDADA,
    /** Devolvida para correção — habilita "Corrigir" (gera nova versão). */
    CORRECAO_SOLICITADA
}
