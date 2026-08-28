package com.github.davidpotentini.enums;

/**
 * Estado de um processo no fluxo do painel de visão geral, derivado das atividades planejadas das
 * suas práticas:
 * <ul>
 *   <li>{@code CONCLUIDO} — há atividades e todas estão concluídas (verde);</li>
 *   <li>{@code EM_ANDAMENTO} — há atividades, algumas concluídas mas nem todas;</li>
 *   <li>{@code NAO_INICIADO} — nenhuma atividade concluída, ou processo sem atividades (cinza).</li>
 * </ul>
 */
public enum EEstadoProcesso {
    CONCLUIDO,
    EM_ANDAMENTO,
    NAO_INICIADO
}
