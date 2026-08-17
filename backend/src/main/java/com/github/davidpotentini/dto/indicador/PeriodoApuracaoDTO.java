package com.github.davidpotentini.dto.indicador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Período na tela de apuração: a meta estipulada + a janela de apuração e, se já apurado, o resultado
 * ({@code resultadoValor}) com quem registrou ({@code registradoPor}) e quando ({@code dataRegistro}).
 * Campos de resultado ficam {@code null} enquanto o período não foi apurado.
 */
public record PeriodoApuracaoDTO(
        Long metCod,
        BigDecimal metaValor,
        LocalDate dataInicioApuracao,
        LocalDate dataFimApuracao,
        BigDecimal resultadoValor,
        String registradoPor,
        LocalDateTime dataRegistro) {
}
