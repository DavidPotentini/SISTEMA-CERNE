package com.github.davidpotentini.cerne2.mapper.endereco;

import com.github.davidpotentini.cerne2.dto.endereco.EnderecoDTO;
import com.github.davidpotentini.cerne2.models.enderecos.EnderecosModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    EnderecoDTO toDTO(EnderecosModel enderecosModel);

    @Mapping(target = "endCod", source = "endCod")
    EnderecosModel toModel(EnderecoDTO enderecoDTO, Long endCod);
}
