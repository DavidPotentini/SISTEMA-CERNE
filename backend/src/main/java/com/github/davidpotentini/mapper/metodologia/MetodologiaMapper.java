package com.github.davidpotentini.mapper.metodologia;

import com.github.davidpotentini.dto.metodologia.AgrupamentoDTO;
import com.github.davidpotentini.dto.metodologia.AtividadeMetodologiaDTO;
import com.github.davidpotentini.dto.metodologia.IndicadorDTO;
import com.github.davidpotentini.dto.metodologia.PraticaDTO;
import com.github.davidpotentini.dto.metodologia.ProcessoDTO;
import com.github.davidpotentini.model.metodologia.AgrupamentoModel;
import com.github.davidpotentini.model.metodologia.AtividadeMetodologiaModel;
import com.github.davidpotentini.model.metodologia.IndicadorMetodologiaModel;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MetodologiaMapper {

    PraticaDTO toDTO(PraticaModel pratica);

    List<PraticaDTO> toDTOList(List<PraticaModel> praticas);

    ProcessoDTO toDTO(ProcessoModel processo, List<PraticaDTO> praticas);

    @Mapping(target = "prcCod", ignore = true)
    @Mapping(target = "nivel", ignore = true)
    @Mapping(target = "ordem", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    ProcessoModel toModel(ProcessoDTO dto);

    @Mapping(target = "prtCod", ignore = true)
    @Mapping(target = "prcCod", ignore = true)
    @Mapping(target = "ordem", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    PraticaModel toModel(PraticaDTO dto);

    List<PraticaModel> toModelList(List<PraticaDTO> dtos);

    @Mapping(target = "prcCod", ignore = true)
    @Mapping(target = "nivel", ignore = true)
    @Mapping(target = "ordem", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    void atualizar(ProcessoDTO dto, @MappingTarget ProcessoModel processo);

    @Mapping(target = "prtCod", ignore = true)
    @Mapping(target = "prcCod", ignore = true)
    @Mapping(target = "ordem", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    void atualizar(PraticaDTO dto, @MappingTarget PraticaModel pratica);

    AgrupamentoDTO toDTO(AgrupamentoModel agrupamento, String vinculoMetodologico);

    @Mapping(target = "agrCod", ignore = true)
    @Mapping(target = "ordem", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    AgrupamentoModel toModel(AgrupamentoDTO dto);

    @Mapping(target = "agrCod", ignore = true)
    @Mapping(target = "prtCod", ignore = true)
    @Mapping(target = "ordem", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    void atualizar(AgrupamentoDTO dto, @MappingTarget AgrupamentoModel agrupamento);

    IndicadorDTO toDTO(IndicadorMetodologiaModel indicador, String vinculoMetodologico);

    @Mapping(target = "inmCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    @Mapping(target = "periodicidade", source = "periodicidade", defaultValue = "NAO_SE_APLICA")
    IndicadorMetodologiaModel toModel(IndicadorDTO dto);

    @Mapping(target = "inmCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    @Mapping(target = "periodicidade", source = "periodicidade", defaultValue = "NAO_SE_APLICA")
    void atualizar(IndicadorDTO dto, @MappingTarget IndicadorMetodologiaModel indicador);

    AtividadeMetodologiaDTO toDTO(AtividadeMetodologiaModel atividade, String vinculoMetodologico);

    @Mapping(target = "ameCod", ignore = true)
    @Mapping(target = "ordem", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    AtividadeMetodologiaModel toModel(AtividadeMetodologiaDTO dto);

    @Mapping(target = "ameCod", ignore = true)
    @Mapping(target = "ordem", ignore = true)
    @Mapping(target = "porEmpreendimento", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    void atualizar(AtividadeMetodologiaDTO dto, @MappingTarget AtividadeMetodologiaModel atividade);
}
