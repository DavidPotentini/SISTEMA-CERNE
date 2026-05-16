package com.github.davidpotentini.cerne2.mapper.canvas;

import com.github.davidpotentini.cerne2.dto.canvas.AmbienteCanvasDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.canvas.AmbienteCanvasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper (componentModel = "spring",
         uses = EntityReferenceResolver.class)
public interface AmbienteCanvasMapper {

    @Mapping(source = "incubadasModel.incCod", target = "incCod")
    AmbienteCanvasDTO toDTO(AmbienteCanvasModel ambienteCanvasModel);

    List<AmbienteCanvasDTO> toDTOList(List<AmbienteCanvasModel> ambienteCanvasModelList);

    @Mapping(source = "ambienteCanvasDTO.incCod", target = "incubadasModel")
    @Mapping(target = "customerPersonasCanvasModelList", ignore = true)
    @Mapping(target = "channelImplementationCanvasModelList", ignore = true)
    AmbienteCanvasModel toModel(AmbienteCanvasDTO ambienteCanvasDTO, Long ambcCod);
}
