package com.github.davidpotentini.enums;

/** Espelha o tipo Postgres {@code VLD_STATUS_MODELO}. Persistido como STRING. */
public enum EStatusModelo {
    /** Modelo em edição — atividades podem ser incluídas/editadas/desativadas. */
    RASCUNHO,
    /** Modelo publicado (imutável) — não sofre nenhuma edição. */
    PUBLICADO
}
