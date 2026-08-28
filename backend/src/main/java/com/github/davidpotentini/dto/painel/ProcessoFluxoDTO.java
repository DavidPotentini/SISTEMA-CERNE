package com.github.davidpotentini.dto.painel;

import com.github.davidpotentini.enums.EEstadoProcesso;

/**
 * Um nó do fluxo de processos no painel de visão geral: o processo do ciclo, seu estado (derivado das
 * atividades das suas práticas) e a contagem concluídas/total que alimenta o rótulo "x/y".
 */
public record ProcessoFluxoDTO(
        Long prccCod,
        String nome,
        int ordem,
        EEstadoProcesso estado,
        long atividadesConcluidas,
        long atividadesTotal) {
}
