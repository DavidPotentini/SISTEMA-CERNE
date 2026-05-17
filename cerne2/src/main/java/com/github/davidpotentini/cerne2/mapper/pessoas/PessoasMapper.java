package com.github.davidpotentini.cerne2.mapper.pessoas;

import com.github.davidpotentini.cerne2.dto.pessoas.PessoasDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EntityReferenceResolver.class)
public interface PessoasMapper {

    @Mapping(source = "incubadasModel.incCod", target = "incCod")
    @Mapping(source = "tipoEmpreendimento", target = "eTipoEmpreendimento")
    PessoasDTO toDTO(PessoasModel pessoasModel);

    List<PessoasDTO> toDTOList(List<PessoasModel> pessoasModelList);

    @Mapping(target = "pessoaCod", source = "pessoaCod")
    @Mapping(source = "pessoasDTO.incCod", target = "incubadasModel")
    @Mapping(source = "pessoasDTO.eTipoEmpreendimento", target = "tipoEmpreendimento")
    @Mapping(target = "EPessoaExterna", ignore = true)
    @Mapping(target = "tarefasModelList", ignore = true)
    @Mapping(target = "formularioRespostasModelList", ignore = true)
    PessoasModel toModel(PessoasDTO pessoasDTO, Long pessoaCod);
}
