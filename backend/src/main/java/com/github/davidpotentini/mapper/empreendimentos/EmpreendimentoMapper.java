package com.github.davidpotentini.mapper.empreendimentos;

import com.github.davidpotentini.dto.empreendimentos.EmpreendimentoDTO;
import com.github.davidpotentini.dto.empreendimentos.PessoaEmpreendimentoDTO;
import com.github.davidpotentini.model.empreendimentos.EmpreendimentosModel;
import com.github.davidpotentini.model.empreendimentos.PessoaEmpreendimentoModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmpreendimentoMapper {

    @Mapping(target = "pessoas", ignore = true)
    @Mapping(target = "cicCod", source = "cicCod")
    EmpreendimentoDTO toDTO(EmpreendimentosModel empreendimento, Long cicCod);

    @Mapping(target = "empCod", ignore = true)
    @Mapping(target = "estagio", source = "estagio", defaultValue = "IDEACAO")
    @Mapping(target = "status", source = "status", defaultValue = "ATIVO")
    EmpreendimentosModel toModel(EmpreendimentoDTO dto);

    @Mapping(target = "empCod", ignore = true)
    @Mapping(target = "estagio", source = "estagio", defaultValue = "IDEACAO")
    @Mapping(target = "status", source = "status", defaultValue = "ATIVO")
    void atualizar(EmpreendimentoDTO dto, @MappingTarget EmpreendimentosModel empreendimento);

    PessoaEmpreendimentoDTO toDTO(PessoaEmpreendimentoModel pessoa);

    List<PessoaEmpreendimentoDTO> toDTOList(List<PessoaEmpreendimentoModel> pessoas);

    @Mapping(target = "pseCod", ignore = true)
    @Mapping(target = "empCod", ignore = true)
    @Mapping(target = "representanteLegal", ignore = true)
    PessoaEmpreendimentoModel toModel(PessoaEmpreendimentoDTO dto);

    List<PessoaEmpreendimentoModel> toModelList(List<PessoaEmpreendimentoDTO> dtos);
}
