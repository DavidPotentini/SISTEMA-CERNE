package com.github.davidpotentini.dto.indicador;

import com.github.davidpotentini.enums.EPeriodicidade;

import java.math.BigDecimal;

public record PainelIndicadorDTO(
        Long indCod,
        String nome,
        String processoNome,
        String praticaNome,
        EPeriodicidade periodicidade,
        String unidade,
        boolean temMeta,
        BigDecimal metaTotal,
        BigDecimal atingidoTotal,
        boolean atingido,
        boolean pendente) {
}
