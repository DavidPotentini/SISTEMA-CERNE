package com.github.davidpotentini.mapper.monitoramento;

import com.github.davidpotentini.dto.monitoramento.PontuacaoDTO;
import com.github.davidpotentini.dto.monitoramento.RodadaDTO;
import com.github.davidpotentini.model.monitoramento.PontuacaoModel;
import com.github.davidpotentini.model.monitoramento.RodadaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Conversões do monitoramento. A rodada tem o {@code responsavelNome} derivado (equipe → conta),
 * então o service resolve o nome e passa pronto na leitura (por isso não há {@code toDTOList} de
 * rodada — a listagem fica orquestrada no service). {@code empCods} só existe na entrada. A aplicação
 * ({@code AplicacaoDTO}) é montada à mão no service por juntar empreendimento, avaliação e pontuações.
 */
@Mapper(componentModel = "spring")
public interface MonitoramentoMapper {

    // ---- rodada ----

    @Mapping(target = "empCods", ignore = true)
    RodadaDTO toDTO(RodadaModel rodada, String responsavelNome);

    @Mapping(target = "rodCod", ignore = true)
    @Mapping(target = "cicCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    @Mapping(target = "tipo", source = "tipo", defaultValue = "PERIODICO")
    RodadaModel toModel(RodadaDTO dto);

    // ---- pontuações ----

    PontuacaoDTO toDTO(PontuacaoModel pontuacao);

    List<PontuacaoDTO> toDTOList(List<PontuacaoModel> pontuacoes);
}
