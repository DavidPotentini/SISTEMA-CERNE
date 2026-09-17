package com.github.davidpotentini.dto.monitoramento;

import com.github.davidpotentini.enums.ESituacaoRodada;
import com.github.davidpotentini.enums.ETipoRodada;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record RodadaDTO(
        Long rodCod,
        @NotBlank String nome,
        ETipoRodada tipo,
        LocalDate prazo,
        Long respPesCod,
        String responsavelNome,
        ESituacaoRodada situacao,
        List<Long> empCods
) {
}
