package com.github.davidpotentini.enums;

/** Espelha o tipo Postgres {@code VLD_SITUACAO_VERSAO}. Persistido como STRING. */
public enum ESituacaoVersao {
    /** Versão de trabalho — única e sempre editável. As abas editam esta. */
    RASCUNHO,
    /** Última versão publicada (imutável). É a que a criação de modelos consome. */
    VIGENTE,
    /** Publicações anteriores. */
    HISTORICA
}
