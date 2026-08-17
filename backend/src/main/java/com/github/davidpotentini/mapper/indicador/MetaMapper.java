package com.github.davidpotentini.mapper.indicador;

import com.github.davidpotentini.dto.indicador.MetaDTO;
import com.github.davidpotentini.model.indicador.MetaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/** Conversão de metas. Na volta ({@code toModel}/{@code atualizar}) chave e indicador são do service. */
@Mapper(componentModel = "spring")
public interface MetaMapper {

    MetaDTO toDTO(MetaModel meta);

    @Mapping(target = "metCod", ignore = true)
    @Mapping(target = "indCod", ignore = true)
    MetaModel toModel(MetaDTO dto);

    @Mapping(target = "metCod", ignore = true)
    @Mapping(target = "indCod", ignore = true)
    void atualizar(MetaDTO dto, @MappingTarget MetaModel meta);
}
