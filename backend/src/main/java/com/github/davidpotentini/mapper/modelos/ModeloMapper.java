package com.github.davidpotentini.mapper.modelos;

import com.github.davidpotentini.dto.modelos.AtividadeModeloDTO;
import com.github.davidpotentini.dto.modelos.ModeloDTO;
import com.github.davidpotentini.dto.modelos.ModeloPraticaDTO;
import com.github.davidpotentini.dto.modelos.ModeloProcessoDTO;
import com.github.davidpotentini.model.metodologia.PraticaModel;
import com.github.davidpotentini.model.metodologia.ProcessoModel;
import com.github.davidpotentini.model.modelos.AtividadeModeloModel;
import com.github.davidpotentini.model.modelos.ModeloModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Conversão de modelos. Na escrita, {@code status}/{@code publicadoEm} ficam por conta do service. A
 * estrutura (processo/prática) é herdada da metodologia — o service carrega práticas/atividades e
 * passa prontas ao montar os DTOs de árvore.
 */
@Mapper(componentModel = "spring")
public interface ModeloMapper {

    ModeloDTO toDTO(ModeloModel modelo);

    /** Criação: status/publicadoEm são setados no service; periodicidade nasce ANUAL. */
    @Mapping(target = "modCod", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "publicadoEm", ignore = true)
    @Mapping(target = "periodicidade", source = "periodicidade", defaultValue = "ANUAL")
    ModeloModel toModel(ModeloDTO dto);

    /** Edição do cabeçalho (só no RASCUNHO): nome/periodicidade/descrição; preserva o resto. */
    @Mapping(target = "modCod", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "publicadoEm", ignore = true)
    @Mapping(target = "periodicidade", source = "periodicidade", defaultValue = "ANUAL")
    void atualizar(ModeloDTO dto, @MappingTarget ModeloModel modelo);

    // ---- atividade ----

    AtividadeModeloDTO toDTO(AtividadeModeloModel atividade);

    List<AtividadeModeloDTO> toDTOList(List<AtividadeModeloModel> atividades);

    /** Criação: modCod/prtCod vêm da rota e situacao nasce ATIVO — setados no service. */
    @Mapping(target = "atmCod", ignore = true)
    @Mapping(target = "modCod", ignore = true)
    @Mapping(target = "prtCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    AtividadeModeloModel toModel(AtividadeModeloDTO dto);

    /** Edição: nome/descrição/responsável; preserva código, vínculo e situação. */
    @Mapping(target = "atmCod", ignore = true)
    @Mapping(target = "modCod", ignore = true)
    @Mapping(target = "prtCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    void atualizar(AtividadeModeloDTO dto, @MappingTarget AtividadeModeloModel atividade);

    // ---- árvore (herdada da metodologia + atividades) ----

    ModeloPraticaDTO toDTO(PraticaModel pratica, List<AtividadeModeloDTO> atividades);

    ModeloProcessoDTO toDTO(ProcessoModel processo, List<ModeloPraticaDTO> praticas);
}
