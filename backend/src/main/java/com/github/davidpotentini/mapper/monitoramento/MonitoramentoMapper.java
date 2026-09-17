package com.github.davidpotentini.mapper.monitoramento;

import com.github.davidpotentini.dto.monitoramento.PontuacaoDTO;
import com.github.davidpotentini.dto.monitoramento.RodadaDTO;
import com.github.davidpotentini.model.monitoramento.PontuacaoModel;
import com.github.davidpotentini.model.monitoramento.RodadaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MonitoramentoMapper {

    @Mapping(target = "empCods", ignore = true)
    RodadaDTO toDTO(RodadaModel rodada, String responsavelNome);

    @Mapping(target = "rodCod", ignore = true)
    @Mapping(target = "cicCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    @Mapping(target = "tipo", source = "tipo", defaultValue = "PERIODICO")
    RodadaModel toModel(RodadaDTO dto);

    PontuacaoDTO toDTO(PontuacaoModel pontuacao);

    List<PontuacaoDTO> toDTOList(List<PontuacaoModel> pontuacoes);
}
