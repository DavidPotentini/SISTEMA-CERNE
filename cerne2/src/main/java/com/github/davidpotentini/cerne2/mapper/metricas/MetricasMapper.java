package com.github.davidpotentini.cerne2.mapper.metricas;

import com.github.davidpotentini.cerne2.dto.metricas.MetricasDTO;
import com.github.davidpotentini.cerne2.models.metricas.MetricasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = IndicadoresMetricasMapper.class)
public interface MetricasMapper {

    @Mapping(source = "indicadoresMetricasModelList", target = "indicadoresMetricasDTOList")
    MetricasDTO toDTO(MetricasModel metricasModel);

    List<MetricasDTO> toDTOList(List<MetricasModel> metricasModelList);

    @Mapping(target = "indicadoresMetricasModelList", ignore = true)
    MetricasModel toModel(MetricasDTO metricasDTO);
}
