package com.github.davidpotentini.dto.indicador;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ResultadoEntradaDTO(
        @NotNull BigDecimal valor) {
}
