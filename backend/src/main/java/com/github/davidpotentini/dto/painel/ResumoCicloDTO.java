package com.github.davidpotentini.dto.painel;

import java.util.List;

/**
 * Resumo de andamento do ciclo em foco para o painel de visão geral (um endpoint, um DTO agregador).
 *
 * <p>Todas as contagens são do ciclo em foco: atividades vêm do planejamento vigente; evidências, das
 * versões correntes das atividades desse plano; indicadores reusam a regra de "meta atingida" da
 * apuração ({@code atingido = resultado ≥ meta}). {@code empreendimentosAtivos} é da incubadora (os
 * empreendimentos não são por ciclo). Sem ciclo em foco, tudo vem zerado e {@code cicloNome} nulo.
 */
public record ResumoCicloDTO(
        String cicloNome,
        long atividadesConcluidas,
        long atividadesTotal,
        int progresso,
        long empreendimentosAtivos,
        long evidenciasRegistradas,
        long evidenciasValidadas,
        long indicadoresAtingidos,
        long indicadoresComMeta,
        List<ProcessoFluxoDTO> fluxo) {
}
