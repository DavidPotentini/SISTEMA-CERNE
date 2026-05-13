package com.github.davidpotentini.cerne2.mapper.formularios;

import com.github.davidpotentini.cerne2.dto.formulario.FormularioDTO;
import com.github.davidpotentini.cerne2.models.formularios.FormularioModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FormularioMapper {

    FormularioDTO toDTO(FormularioModel formularioModel);

    List<FormularioDTO> toDTOList(List<FormularioModel> formularioModelList);

    @Mapping(target = "formularioRespostasModelList", ignore = true)
    @Mapping(source = "frmCod", target = "frmCod")
    FormularioModel toModel(FormularioDTO formularioDTO, Long frmCod);
}
