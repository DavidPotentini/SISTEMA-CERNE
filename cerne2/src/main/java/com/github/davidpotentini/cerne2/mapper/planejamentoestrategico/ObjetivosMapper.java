package com.github.davidpotentini.cerne2.mapper.planejamentoestrategico;

import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.ObjetivosDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.ObjetivosModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EntityReferenceResolver.class)
public interface ObjetivosMapper {

    @Mapping(source = "projetosModel.prjCod", target = "prjCod")
    ObjetivosDTO toDTO(ObjetivosModel objetivosModel);

    List<ObjetivosDTO> toDTOList(List<ObjetivosModel> objetivosModelList);

    @Mapping(target = "objCod", source = "objCod")
    @Mapping(source = "prjCod", target = "projetosModel")
    @Mapping(target = "tarefasModelList", ignore = true)
    @Mapping(target = "indicadoresMetricasModelList", ignore = true)
    ObjetivosModel toModel(ObjetivosDTO objetivosDTO, Long objCod, Long prjCod);
}
