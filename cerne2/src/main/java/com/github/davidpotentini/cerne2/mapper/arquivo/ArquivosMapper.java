package com.github.davidpotentini.cerne2.mapper.arquivo;

import com.github.davidpotentini.cerne2.dto.arquivo.ArquivoDTO;
import com.github.davidpotentini.cerne2.models.arquivo.ArquivosModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArquivosMapper {

    @Mapping(source = "tamanhoBytes", target = "tamanhoType")
    ArquivoDTO toDTO(ArquivosModel arquivosModel);

    List<ArquivoDTO> toDTOList(List<ArquivosModel> arquivosModelList);

    @Mapping(source = "tamanhoType", target = "tamanhoBytes")
    @Mapping(target = "storageKey", ignore = true)
    @Mapping(target = "arquivosIncubadasModelList", ignore = true)
    @Mapping(target = "arquivosEvidenciasModelList", ignore = true)
    ArquivosModel toModel(ArquivoDTO arquivoDTO);
}
