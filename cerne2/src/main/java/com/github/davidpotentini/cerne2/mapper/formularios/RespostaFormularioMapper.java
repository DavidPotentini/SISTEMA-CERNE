package com.github.davidpotentini.cerne2.mapper.formularios;

import com.github.davidpotentini.cerne2.dto.formulario.FormularioRespostaDTO;
import com.github.davidpotentini.cerne2.models.formularios.FormularioRespostasModel;
import com.github.davidpotentini.cerne2.models.formularios.ids.FormularioRespostasId;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RespostaFormularioMapper {

    @Mapping(source = "formularioRespostasId.frmCod", target = "frmCod")
    @Mapping(source = "formularioRespostasId.pessoaCod", target = "pessoaCod")
    FormularioRespostaDTO toDTO(FormularioRespostasModel formularioRespostasModel);

    List<FormularioRespostaDTO> toDTOList(List<FormularioRespostasModel> formularioRespostasModelList);

    @Mapping(target = "formularioRespostasId", ignore = true)
    @Mapping(target = "pessoasModel", ignore = true)
    @Mapping(target = "formularioModel", ignore = true)
    FormularioRespostasModel toModel(FormularioRespostaDTO formularioRespostaDTO, Long frmCod, Long pessoaCod);

    @AfterMapping
    default void preencherId(Long frmCod,
                           Long pessoaCod,
                           @MappingTarget FormularioRespostasModel model){
        FormularioRespostasId id = new FormularioRespostasId();

        id.setFrmCod(frmCod);
        id.setPessoaCod(pessoaCod);

        model.setFormularioRespostasId(id);
    }
}
