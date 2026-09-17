package com.github.davidpotentini.dto.indicador;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MetaDTO(
        Long metCod,
        @NotNull BigDecimal valor,
        @NotNull LocalDate dataInicioApuracao,
        @NotNull LocalDate dataFimApuracao) {
}
