package com.github.davidpotentini.cerne2.mapper.planejamentoestrategico;

import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.TarefasDTO;
import com.github.davidpotentini.cerne2.mapper.EntityReferenceResolver;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.TarefasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EntityReferenceResolver.class)
public interface TarefasMapper {

    @Mapping(source = "objetivosModel.objCod", target = "objCod")
    @Mapping(source = "pessoasModel.pessoaCod", target = "respCod")
    @Mapping(source = "ESituacaoTarefa", target = "eSituacaoTarefa")
    TarefasDTO toDTO(TarefasModel tarefasModel);

    List<TarefasDTO> toDTOList(List<TarefasModel> tarefasModelList);

    @Mapping(target = "trfCod", source = "trfCod")
    @Mapping(source = "objCod", target = "objetivosModel")
    @Mapping(source = "tarefasDTO.respCod", target = "pessoasModel")
    @Mapping(source = "tarefasDTO.eSituacaoTarefa", target = "ESituacaoTarefa")
    @Mapping(target = "evidenciasModelList", ignore = true)
    TarefasModel toModel(TarefasDTO tarefasDTO, Long trfCod, Long objCod);
}
