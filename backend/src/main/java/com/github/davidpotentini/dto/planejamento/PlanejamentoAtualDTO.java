package com.github.davidpotentini.dto.planejamento;

/**
 * Situação do planejamento do ciclo ativo, para a tela decidir o que exibir: se há ciclo ativo
 * ({@code cicloAtivo}) e, em caso afirmativo, o {@code planejamento} vigente ({@code null} quando
 * ainda não foi gerado — mostra o "Gerar de modelo").
 */
public record PlanejamentoAtualDTO(
        boolean cicloAtivo,
        Long cicCod,
        String cicloNome,
        PlanejamentoDTO planejamento
) {
}
