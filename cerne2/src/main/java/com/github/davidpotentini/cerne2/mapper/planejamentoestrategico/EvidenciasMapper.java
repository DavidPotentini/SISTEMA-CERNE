package com.github.davidpotentini.cerne2.mapper.planejamentoestrategico;

import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.EvidenciasDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.EvidenciasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EntityReferenceResolver.class)
public interface EvidenciasMapper {

    @Mapping(source = "tarefasModel.trfCod", target = "trfCod")
    EvidenciasDTO toDTO(EvidenciasModel evidenciasModel);

    List<EvidenciasDTO> toDTOList(List<EvidenciasModel> evidenciasModelList);

    @Mapping(target = "evdCod", source = "evdCod")
    @Mapping(source = "evidenciasDTO.trfCod", target = "tarefasModel")
    EvidenciasModel toModel(EvidenciasDTO evidenciasDTO, Long evdCod);
}
