package com.github.davidpotentini.cerne2.mapper.planejamentoestrategico;

import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.ProjetosDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.ProjetosModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EntityReferenceResolver.class)
public interface ProjetosMapper {

    @Mapping(source = "planejamentoEstrategicoModel.pesCod", target = "pesCod")
    ProjetosDTO toDTO(ProjetosModel projetosModel);

    List<ProjetosDTO> toDTOList(List<ProjetosModel> projetosModelList);

    @Mapping(target = "prjCod", source = "prjCod")
    @Mapping(source = "pesCod", target = "planejamentoEstrategicoModel")
    @Mapping(target = "objetivosModelList", ignore = true)
    ProjetosModel toModel(ProjetosDTO projetosDTO, Long prjCod, Long pesCod);
}
