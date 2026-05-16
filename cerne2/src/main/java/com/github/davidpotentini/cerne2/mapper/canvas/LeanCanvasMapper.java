package com.github.davidpotentini.cerne2.mapper.canvas;

import com.github.davidpotentini.cerne2.dto.canvas.LeanCanvasDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.canvas.LeanCanvasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EntityReferenceResolver.class)
public interface LeanCanvasMapper {

    @Mapping(source = "ambienteCanvasModel.ambcCod", target = "ambcCod")
    LeanCanvasDTO toDTO(LeanCanvasModel leanCanvasModel);

    List<LeanCanvasDTO> toDTOList(List<LeanCanvasModel> leanCanvasModelList);

    @Mapping(target = "leanCod", source = "leanCod")
    @Mapping(target = "ambienteCanvasModel", source = "ambcCod")
    LeanCanvasModel toModel(LeanCanvasDTO leanCanvasDTO, Long leanCod, Long ambcCod);
}
