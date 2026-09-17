package com.github.davidpotentini.dto.planejamento;

import com.github.davidpotentini.enums.EStatusPlanejamento;

import java.time.LocalDate;

public record PlanejamentoDTO(
        Long plnCod,
        String nome,
        Long cicCod,
        String cicloNome,
        EStatusPlanejamento status,
        LocalDate inicio,
        LocalDate fim,
        Long respPesCod,
        String responsavel,
        int totalAtividades,
        int concluidas,
        int progresso
) {
}
