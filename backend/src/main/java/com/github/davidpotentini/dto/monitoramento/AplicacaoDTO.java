package com.github.davidpotentini.dto.monitoramento;

import com.github.davidpotentini.enums.ERecomendacaoMonitor;
import com.github.davidpotentini.enums.EStatusMonitoramento;

import java.time.LocalDate;
import java.util.List;

public record AplicacaoDTO(
        Long avaCod,
        Long empCod,
        String empNome,
        EStatusMonitoramento status,
        LocalDate data,
        ERecomendacaoMonitor recomendacao,
        String observacao,
        List<PontuacaoDTO> pontuacoes
) {
}
