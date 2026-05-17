package com.github.davidpotentini.cerne2.dto.metricas;

import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.ObjetivosDTO;

import java.math.BigDecimal;
import java.util.List;

public record IndicadoresMetricasDTO(Long indCod,
                                     String descricao,
                                     BigDecimal meta,
                                     Long metCod,
                                     ObjetivosDTO objetivosDTOResponse,
                                     List<QuantidadeMensalMetricasDTO> quantidadeMensalMetricasDTOList) {
}
