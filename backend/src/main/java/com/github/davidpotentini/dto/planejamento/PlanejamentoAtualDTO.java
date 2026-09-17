package com.github.davidpotentini.dto.planejamento;

public record PlanejamentoAtualDTO(
        boolean cicloAtivo,
        Long cicCod,
        String cicloNome,
        PlanejamentoDTO planejamento
) {
}
