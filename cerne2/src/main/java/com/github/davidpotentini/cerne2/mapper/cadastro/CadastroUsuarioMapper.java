package com.github.davidpotentini.cerne2.mapper.cadastro;

import com.github.davidpotentini.cerne2.dto.cadastro.CadastroUsuarioDTO;
import com.github.davidpotentini.cerne2.models.cadastro.TenantsModel;
import com.github.davidpotentini.cerne2.models.cadastro.UsersModel;
import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CadastroUsuarioMapper {

    @Mapping(target = "usnCod", ignore = true)
    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "tenantsModel", ignore = true)
    UsersModel toUserModel(CadastroUsuarioDTO cadastroUsuarioDTO);

    @Mapping(target = "tntCod", source = "tntCod")
    @Mapping(target = "nomeSchema", expression = "java(cadastroUsuarioDTO.nomeEmpresa() + \"_schema\")")
    @Mapping(target = "usersModel", ignore = true)
    TenantsModel toTenantsModel(CadastroUsuarioDTO cadastroUsuarioDTO);

    @Mapping(target = "pessoaCod", ignore = true)
    @Mapping(target = "cargo", ignore = true)
    @Mapping(target = "EPessoaExterna", constant = "NAO")
    @Mapping(target = "tipoEmpreendimento", constant = "INCUBADORA")
    @Mapping(target = "incubadasModel", ignore = true)
    @Mapping(target = "tarefasModelList", ignore = true)
    @Mapping(target = "formularioRespostasModelList", ignore = true)
    PessoasModel toPessoaModel(CadastroUsuarioDTO cadastroUsuarioDTO);
}
