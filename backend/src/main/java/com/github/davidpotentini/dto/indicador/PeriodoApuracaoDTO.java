package com.github.davidpotentini.dto.indicador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PeriodoApuracaoDTO(
        Long metCod,
        BigDecimal metaValor,
        LocalDate dataInicioApuracao,
        LocalDate dataFimApuracao,
        BigDecimal resultadoValor,
        String registradoPor,
        LocalDateTime dataRegistro) {
}
