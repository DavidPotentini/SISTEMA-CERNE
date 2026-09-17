package com.github.davidpotentini.dto.monitoramento;

import java.time.LocalDate;
import java.util.List;

public record EvolucaoRodadaDTO(
        Long rodCod,
        String rodadaNome,
        LocalDate data,
        List<PontuacaoDTO> pontuacoes
) {
}
