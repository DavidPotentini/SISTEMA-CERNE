package com.github.davidpotentini.cerne2.mapper.canvas;

import com.github.davidpotentini.cerne2.dto.canvas.ChannelImplementationCanvasDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.canvas.ChannelImplementationCanvasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EntityReferenceResolver.class)
public interface ChannelImplementationCanvasMapper {

//    @Mapping(source = "channelImplementationCanvasId.segCod", target = "segCod")
    @Mapping(source = "ambienteCanvasModel.ambcCod", target = "ambcCod")
    ChannelImplementationCanvasDTO toDTO(ChannelImplementationCanvasModel channelImplementationCanvasModel);

    List<ChannelImplementationCanvasDTO> toDTOList(List<ChannelImplementationCanvasModel> channelImplementationCanvasModel);

    @Mapping(source = "ambcCod", target = "ambienteCanvasModel")
//    @Mapping(source = "segCod", target = "channelImplementationCanvasId.segCod")
    ChannelImplementationCanvasModel toModel(ChannelImplementationCanvasDTO channelImplementationCanvasDTO, Long segCod, Long ambcCod);
}
