package com.github.davidpotentini.mapper.empreendimentos;

import com.github.davidpotentini.dto.empreendimentos.EmpreendimentoDTO;
import com.github.davidpotentini.dto.empreendimentos.PessoaEmpreendimentoDTO;
import com.github.davidpotentini.model.empreendimentos.EmpreendimentosModel;
import com.github.davidpotentini.model.empreendimentos.PessoaEmpreendimentoModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Conversão de empreendimento e suas pessoas. O {@code responsavelNome} é derivado (equipe → conta),
 * então o service resolve o nome e passa pronto na leitura — por isso a listagem de empreendimentos
 * fica orquestrada no service (não há {@code toDTOList} para ela). Na escrita, {@code estagio} e
 * {@code situacao} caem para o padrão quando não informados; códigos e vínculos ({@code empCod},
 * {@code principal}) ficam por conta do service.
 */
@Mapper(componentModel = "spring")
public interface EmpreendimentoMapper {

    // ---- empreendimento ----

    EmpreendimentoDTO toDTO(EmpreendimentosModel empreendimento, String responsavelNome);

    @Mapping(target = "empCod", ignore = true)
    @Mapping(target = "estagio", source = "estagio", defaultValue = "IDEACAO")
    @Mapping(target = "situacao", source = "situacao", defaultValue = "EM_ANALISE")
    EmpreendimentosModel toModel(EmpreendimentoDTO dto);

    /** Aplica os campos editáveis sobre o empreendimento existente (edição); preserva {@code empCod}. */
    @Mapping(target = "empCod", ignore = true)
    @Mapping(target = "estagio", source = "estagio", defaultValue = "IDEACAO")
    @Mapping(target = "situacao", source = "situacao", defaultValue = "EM_ANALISE")
    void atualizar(EmpreendimentoDTO dto, @MappingTarget EmpreendimentosModel empreendimento);

    // ---- pessoa do empreendimento ----

    PessoaEmpreendimentoDTO toDTO(PessoaEmpreendimentoModel pessoa);

    List<PessoaEmpreendimentoDTO> toDTOList(List<PessoaEmpreendimentoModel> pessoas);

    @Mapping(target = "pseCod", ignore = true)
    @Mapping(target = "empCod", ignore = true)
    @Mapping(target = "principal", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    PessoaEmpreendimentoModel toModel(PessoaEmpreendimentoDTO dto);

    List<PessoaEmpreendimentoModel> toModelList(List<PessoaEmpreendimentoDTO> dtos);
}
