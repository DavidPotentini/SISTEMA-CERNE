package com.github.davidpotentini.mapper.metodologia;

import com.github.davidpotentini.dto.metodologia.AtividadeMetodologiaDTO;
import com.github.davidpotentini.dto.metodologia.IndicadorDTO;
import com.github.davidpotentini.dto.metodologia.PraticaDTO;
import com.github.davidpotentini.dto.metodologia.ProcessoDTO;
import com.github.davidpotentini.model.metodologia.AtividadeMetodologiaModel;
import com.github.davidpotentini.model.metodologia.IndicadorMetodologiaModel;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Conversão da metodologia. Na escrita, os códigos e a {@code situacao} ("nasce ATIVO") ficam por
 * conta do service; no processo, o {@code prcCod} vem do banco e as {@code praticas} são carregadas
 * pelo service e passadas prontas ao montar o DTO.
 */
@Mapper(componentModel = "spring")
public interface MetodologiaMapper {

    PraticaDTO toDTO(PraticaModel pratica);

    List<PraticaDTO> toDTOList(List<PraticaModel> praticas);

    ProcessoDTO toDTO(ProcessoModel processo, List<PraticaDTO> praticas);

    @Mapping(target = "prcCod", ignore = true)
    @Mapping(target = "ordem", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    ProcessoModel toModel(ProcessoDTO dto);

    @Mapping(target = "prtCod", ignore = true)
    @Mapping(target = "prcCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    PraticaModel toModel(PraticaDTO dto);

    List<PraticaModel> toModelList(List<PraticaDTO> dtos);

    /** Edição de processo: aplica nome/descrição; preserva código, ordem (gerida por arrastar) e situação. */
    @Mapping(target = "prcCod", ignore = true)
    @Mapping(target = "ordem", ignore = true)
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

    // ---- atividade ----

    AtividadeMetodologiaDTO toDTO(AtividadeMetodologiaModel atividade, String vinculoMetodologico);

    @Mapping(target = "ameCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    AtividadeMetodologiaModel toModel(AtividadeMetodologiaDTO dto);

    /** Edição de atividade: aplica prática (vínculo)/nome/observações; preserva código e situação. */
    @Mapping(target = "ameCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    void atualizar(AtividadeMetodologiaDTO dto, @MappingTarget AtividadeMetodologiaModel atividade);
}
