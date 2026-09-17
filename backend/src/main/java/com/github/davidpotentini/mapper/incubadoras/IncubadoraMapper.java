package com.github.davidpotentini.mapper.incubadoras;

import com.github.davidpotentini.dto.incubadoras.IncubadoraDTO;
import com.github.davidpotentini.model.incubadoras.IncubadorasModel;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IncubadoraMapper {

    @Mapping(target = "incCod", ignore = true)
    @Mapping(target = "nomeSchema", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "criadaEm", ignore = true)
    @Mapping(target = "ativadaEm", ignore = true)
    void atualizar(IncubadoraDTO dto, @MappingTarget IncubadorasModel model);
}
