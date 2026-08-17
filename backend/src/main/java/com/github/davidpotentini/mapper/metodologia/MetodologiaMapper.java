package com.github.davidpotentini.mapper.metodologia;

import com.github.davidpotentini.dto.metodologia.IndicadorDTO;
import com.github.davidpotentini.dto.metodologia.PraticaDTO;
import com.github.davidpotentini.dto.metodologia.ProcessoDTO;
import com.github.davidpotentini.dto.metodologia.VersaoDTO;
import com.github.davidpotentini.model.metodologia.IndicadorMetodologiaModel;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import com.github.davidpotentini.model.metodologia.VersaoMetodologiaModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Conversão da metodologia. Versão é só leitura (criada internamente). Na escrita, os códigos e a
 * {@code situacao} ("nasce ATIVO") ficam por conta do service; no processo, o {@code prcCod} vem do
 * banco e as {@code praticas} são carregadas pelo service e passadas prontas ao montar o DTO.
 */
@Mapper(componentModel = "spring")
public interface MetodologiaMapper {

    VersaoDTO toDTO(VersaoMetodologiaModel versao, String publicadoPor);

    PraticaDTO toDTO(PraticaModel pratica);

    List<PraticaDTO> toDTOList(List<PraticaModel> praticas);

    ProcessoDTO toDTO(ProcessoModel processo, List<PraticaDTO> praticas);

    @Mapping(target = "prcCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    ProcessoModel toModel(ProcessoDTO dto);

    @Mapping(target = "prtCod", ignore = true)
    @Mapping(target = "prcCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    PraticaModel toModel(PraticaDTO dto);

    List<PraticaModel> toModelList(List<PraticaDTO> dtos);

    /** Edição de processo: aplica ordem/nome/descrição; preserva código, versão e situação. */
    @Mapping(target = "prcCod", ignore = true)
    @Mapping(target = "verCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    void atualizar(ProcessoDTO dto, @MappingTarget ProcessoModel processo);

    /** Edição de prática: aplica nome/descrição; preserva código, vínculo e situação. */
    @Mapping(target = "prtCod", ignore = true)
    @Mapping(target = "prcCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    void atualizar(PraticaDTO dto, @MappingTarget PraticaModel pratica);

    // ---- indicador ----

    IndicadorDTO toDTO(IndicadorMetodologiaModel indicador, String vinculoMetodologico);

    @Mapping(target = "inmCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    @Mapping(target = "periodicidade", source = "periodicidade", defaultValue = "NAO_SE_APLICA")
    IndicadorMetodologiaModel toModel(IndicadorDTO dto);

    /** Edição de indicador: aplica prática (vínculo)/nome/unidade/periodicidade; preserva código e situação. */
    @Mapping(target = "inmCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    @Mapping(target = "periodicidade", source = "periodicidade", defaultValue = "NAO_SE_APLICA")
    void atualizar(IndicadorDTO dto, @MappingTarget IndicadorMetodologiaModel indicador);
}
