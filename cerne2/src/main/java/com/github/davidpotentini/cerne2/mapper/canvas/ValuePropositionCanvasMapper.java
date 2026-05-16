package com.github.davidpotentini.cerne2.mapper.canvas;

import com.github.davidpotentini.cerne2.dto.canvas.ValuePropositionCanvasDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.canvas.ValuePropostionCanvasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EntityReferenceResolver.class)
public interface ValuePropositionCanvasMapper {

    @Mapping(source = "ambienteCanvasModel.ambcCod", target = "ambcCod")
    ValuePropositionCanvasDTO toDTO(ValuePropostionCanvasModel valuePropostionCanvasModel);

    List<ValuePropositionCanvasDTO> toDTOList(List<ValuePropostionCanvasModel> valuePropostionCanvasModelList);

    @Mapping(target = "vpcCod", source = "vpcCod")
    @Mapping(target = "ambienteCanvasModel", source = "ambcCod")
    ValuePropostionCanvasModel toModel(ValuePropositionCanvasDTO valuePropositionCanvasDTO, Long vpcCod, Long ambcCod);
}
