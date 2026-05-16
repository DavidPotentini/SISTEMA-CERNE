package com.github.davidpotentini.cerne2.mapper.canvas;

import com.github.davidpotentini.cerne2.dto.canvas.BusinessModelCanvasDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.canvas.BusinessModelCanvasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EntityReferenceResolver.class)
public interface BusinessModelCanvasMapper {

    @Mapping(source = "ambienteCanvasModel.ambcCod", target = "ambcCod")
    BusinessModelCanvasDTO toDTO(BusinessModelCanvasModel businessModelCanvasModel);

    List<BusinessModelCanvasDTO> toDTOList(List<BusinessModelCanvasModel> businessModelCanvasModelList);

    @Mapping(target = "bmcCod", source = "bmcCod")
    @Mapping(target = "ambienteCanvasModel", source = "ambcCod")
    BusinessModelCanvasModel toModel(BusinessModelCanvasDTO businessModelCanvasDTO, Long bmcCod, Long ambcCod);
}
