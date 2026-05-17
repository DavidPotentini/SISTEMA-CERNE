package com.github.davidpotentini.cerne2.mapper.metricas;

import com.github.davidpotentini.cerne2.dto.metricas.IndicadoresMetricasDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.mapper.planejamentoestrategico.ObjetivosMapper;
import com.github.davidpotentini.cerne2.models.metricas.IndicadoresMetricasModel;
import com.github.davidpotentini.cerne2.models.metricas.MetricasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {EntityReferenceResolver.class, ObjetivosMapper.class, QuantidadeMensalMetricasMapper.class})
public interface IndicadoresMetricasMapper {

    @Mapping(source = "metricasModel.metCod", target = "metCod")
    @Mapping(source = "objetivosModel", target = "objetivosDTOResponse")
    @Mapping(source = "quantidadeMensalMetricasModelList", target = "quantidadeMensalMetricasDTOList")
    IndicadoresMetricasDTO toDTO(IndicadoresMetricasModel indicadoresMetricasModel);

    List<IndicadoresMetricasDTO> toDTOList(List<IndicadoresMetricasModel> indicadoresMetricasModelList);

    @Mapping(source = "indicadoresMetricasDTO.indCod", target = "indCod")
    @Mapping(source = "indicadoresMetricasDTO.descricao", target = "descricao")
    @Mapping(source = "indicadoresMetricasDTO.meta", target = "meta")
    @Mapping(source = "indicadoresMetricasDTO.objetivosDTOResponse.objCod", target = "objetivosModel")
    @Mapping(source = "metricasModel", target = "metricasModel")
    @Mapping(target = "quantidadeMensalMetricasModelList", ignore = true)
    IndicadoresMetricasModel toModel(IndicadoresMetricasDTO indicadoresMetricasDTO,
                                     MetricasModel metricasModel);
}
