package com.github.davidpotentini.cerne2.mapper.servicosvaloragregado;

import com.github.davidpotentini.cerne2.dto.servicosvaloragregado.ServicosValorAgregadoDTO;
import com.github.davidpotentini.cerne2.models.servicosvaloragregado.ServicosValorAgregadoModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServicosValorAgregadoMapper {

    ServicosValorAgregadoDTO toDTO(ServicosValorAgregadoModel servicosValorAgregadoModel);

    List<ServicosValorAgregadoDTO> toDTOList(List<ServicosValorAgregadoModel> servicosValorAgregadoModelList);

    @Mapping(target = "servCod", source = "servCod")
    ServicosValorAgregadoModel toModel(ServicosValorAgregadoDTO servicosValorAgregadoDTO, Long servCod);
}
