package com.github.davidpotentini.cerne2.mapper.metricas;

import com.github.davidpotentini.cerne2.dto.metricas.QuantidadeMensalMetricasDTO;
import com.github.davidpotentini.cerne2.models.metricas.IndicadoresMetricasModel;
import com.github.davidpotentini.cerne2.models.metricas.QuantidadeMensalMetricasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuantidadeMensalMetricasMapper {

    @Mapping(source = "quantidadeMensalMetricasId.mesCod", target = "mesCod")
    QuantidadeMensalMetricasDTO toDTO(QuantidadeMensalMetricasModel quantidadeMensalMetricasModel);

    List<QuantidadeMensalMetricasDTO> toDTOList(List<QuantidadeMensalMetricasModel> quantidadeMensalMetricasModelList);

    @Mapping(source = "quantidadeMensalMetricasDTO.mesCod", target = "quantidadeMensalMetricasId.mesCod")
    @Mapping(source = "indicadoresMetricasModel.indCod", target = "quantidadeMensalMetricasId.indCod")
    @Mapping(source = "indicadoresMetricasModel", target = "indicadoresMetricasModel")
    QuantidadeMensalMetricasModel toModel(QuantidadeMensalMetricasDTO quantidadeMensalMetricasDTO,
                                          IndicadoresMetricasModel indicadoresMetricasModel);
}
