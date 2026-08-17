package com.github.davidpotentini.dto.indicador;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Entrada do registro de resultado de um período: só o {@code valor} apurado. Quem registrou e a data
 * são carimbados no service (usuário logado + agora).
 */
public record ResultadoEntradaDTO(
        @NotNull BigDecimal valor) {
}
