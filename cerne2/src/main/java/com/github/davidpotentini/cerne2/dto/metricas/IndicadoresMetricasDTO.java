package com.github.davidpotentini.cerne2.dto.metricas;

import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.response.ObjetivosDTOResponse;

import java.math.BigDecimal;
import java.util.List;

public record IndicadoresMetricasDTO(Long indCod,
                                     String descricao,
                                     BigDecimal meta,
                                     Long metCod,
                                     ObjetivosDTOResponse objetivosDTOResponse,
                                     List<QuantidadeMensalMetricasDTO> quantidadeMensalMetricasDTOList) {
}
