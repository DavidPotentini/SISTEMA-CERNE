package com.github.davidpotentini.mapper.ciclos;

import com.github.davidpotentini.dto.ciclos.CicloDTO;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CicloMapper {

    CicloDTO toDTO(CiclosModel ciclo);

    List<CicloDTO> toDTOList(List<CiclosModel> ciclos);

    @Mapping(target = "cicCod", ignore = true)
    @Mapping(target = "status", ignore = true)
    CiclosModel toModel(CicloDTO dto);

    List<CiclosModel> toModelList(List<CicloDTO> dtos);
}
