package com.github.davidpotentini.cerne2.mapper.canvas;

import com.github.davidpotentini.cerne2.dto.canvas.CustomerPersonasCanvasDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.canvas.CustomerPersonasCanvasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EntityReferenceResolver.class)
public interface CustomerPersonasCanvasMapper {

//    @Mapping(source = "customerPersonasCanvasId.personaCod", target = "personaCod")
    @Mapping(source = "ambienteCanvasModel.ambcCod", target = "ambcCod")
    CustomerPersonasCanvasDTO toDTO(CustomerPersonasCanvasModel customerPersonasCanvasModel);

    List<CustomerPersonasCanvasDTO> toDTOList(List<CustomerPersonasCanvasModel> customerPersonasCanvasModelList);

    @Mapping(source = "ambcCod", target = "ambienteCanvasModel")
//    @Mapping(source = "personaCod", target = "customerPersonasCanvasId.personaCod")
    CustomerPersonasCanvasModel toModel(CustomerPersonasCanvasDTO customerPersonasCanvasDTO, Long personaCod, Long ambcCod);
}
