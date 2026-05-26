package com.github.davidpotentini.cerne2.mapper.planejamentoestrategico;

import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.PlanejamentoEstrategicoDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.PlanejamentoEstrategicoModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EntityReferenceResolver.class)
public interface PlanejamentoEstrategicoMapper {

    PlanejamentoEstrategicoDTO toDTO(PlanejamentoEstrategicoModel planejamentoEstrategicoModel);

    List<PlanejamentoEstrategicoDTO> toDTOList(List<PlanejamentoEstrategicoModel> planejamentoEstrategicoModelList);

    @Mapping(target = "pesCod", source = "pesCod")
    @Mapping(source = "incCod", target = "incubadasModel")
    @Mapping(target = "projetosModelList", ignore = true)
    PlanejamentoEstrategicoModel toModel(PlanejamentoEstrategicoDTO planejamentoEstrategicoDTO, Long pesCod, Long incCod);
}
