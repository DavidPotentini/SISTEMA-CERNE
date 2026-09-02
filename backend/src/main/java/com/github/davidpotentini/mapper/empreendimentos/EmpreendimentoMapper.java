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
 * Conversão de empreendimento e suas pessoas. Na escrita, {@code estagio} e {@code status} caem para o
 * padrão quando não informados; códigos e vínculos ({@code empCod}, {@code representanteLegal}) ficam
 * por conta do service. A listagem de empreendimentos é orquestrada no service (por isso não há
 * {@code toDTOList} para ela).
 */
@Mapper(componentModel = "spring")
public interface EmpreendimentoMapper {

    // ---- empreendimento ----

    @Mapping(target = "pessoas", ignore = true)
    EmpreendimentoDTO toDTO(EmpreendimentosModel empreendimento);

    @Mapping(target = "empCod", ignore = true)
    @Mapping(target = "estagio", source = "estagio", defaultValue = "IDEACAO")
    @Mapping(target = "status", source = "status", defaultValue = "ATIVO")
    EmpreendimentosModel toModel(EmpreendimentoDTO dto);

    /** Aplica os campos editáveis sobre o empreendimento existente (edição); preserva {@code empCod}. */
    @Mapping(target = "empCod", ignore = true)
    @Mapping(target = "estagio", source = "estagio", defaultValue = "IDEACAO")
    @Mapping(target = "status", source = "status", defaultValue = "ATIVO")
    void atualizar(EmpreendimentoDTO dto, @MappingTarget EmpreendimentosModel empreendimento);

    // ---- pessoa do empreendimento ----

    PessoaEmpreendimentoDTO toDTO(PessoaEmpreendimentoModel pessoa);

    List<PessoaEmpreendimentoDTO> toDTOList(List<PessoaEmpreendimentoModel> pessoas);

    @Mapping(target = "pseCod", ignore = true)
    @Mapping(target = "empCod", ignore = true)
    @Mapping(target = "representanteLegal", ignore = true)
    PessoaEmpreendimentoModel toModel(PessoaEmpreendimentoDTO dto);

    List<PessoaEmpreendimentoModel> toModelList(List<PessoaEmpreendimentoDTO> dtos);
}
