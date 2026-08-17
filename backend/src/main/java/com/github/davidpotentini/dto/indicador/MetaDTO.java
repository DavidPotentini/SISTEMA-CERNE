package com.github.davidpotentini.dto.indicador;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Meta de um indicador para um período (entrada + saída). {@code valor} é a meta estipulada;
 * {@code dataInicioApuracao}/{@code dataFimApuracao} delimitam a janela de apuração. O indicador vem
 * pela rota; não é campo do DTO.
 */
public record MetaDTO(
        Long metCod,
        @NotNull BigDecimal valor,
        @NotNull LocalDate dataInicioApuracao,
        @NotNull LocalDate dataFimApuracao) {
}
