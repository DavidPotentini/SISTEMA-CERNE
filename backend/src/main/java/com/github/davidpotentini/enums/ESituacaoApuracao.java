package com.github.davidpotentini.enums;

/**
 * Situação de apuração de um indicador do ciclo, derivada dos seus períodos (metas):
 * {@code CONCLUIDA} (todos os períodos apurados), {@code ATRASADA} (há período com janela encerrada
 * sem resultado) ou {@code EM_ABERTO} (ainda há período a apurar, mas nenhum atrasado). Indicadores
 * sem período (sem meta) não têm situação.
 */
public enum ESituacaoApuracao {
    EM_ABERTO,
    ATRASADA,
    CONCLUIDA
}
