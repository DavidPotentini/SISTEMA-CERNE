package com.github.davidpotentini.cerne2.dto.metricas;

import java.util.List;

public record MetricasDTO(Long metCod,
                          String descricao,
                          List<IndicadoresMetricasDTO> indicadoresMetricasDTOList) {
}
