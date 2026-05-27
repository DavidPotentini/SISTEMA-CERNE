package com.github.davidpotentini.cerne2.mapper.monitoramentoempreendedor;

import com.github.davidpotentini.cerne2.dto.monitoramentoempreendedor.PerguntasMonitoramentoEmpreendedorDTO;
import com.github.davidpotentini.cerne2.mapper.metricas.IndicadoresMetricasMapper;
import com.github.davidpotentini.cerne2.models.monitoramentoempreendedor.PerguntasMonitoramentoEmpreendedorModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = IndicadoresMetricasMapper.class)
public interface PerguntasMonitoramentoEmpreendedorMapper {
/*
    @Mapping(source = "fasesMonitoramentoEmpreendedorModel.fasCod", target = "fasCod")
    PerguntasMonitoramentoEmpreendedorDTO toDTO(PerguntasMonitoramentoEmpreendedorModel perguntasMonitoramentoEmpreendedorModel);

    List<PerguntasMonitoramentoEmpreendedorDTO> toDTOList(List<PerguntasMonitoramentoEmpreendedorModel> perguntasMonitoramentoEmpreendedorModelList);

    @Mapping(source = "fasCod", target = "fasesMonitoramentoEmpreendedorModel")
    PerguntasMonitoramentoEmpreendedorModel toModel(PerguntasMonitoramentoEmpreendedorDTO perguntasMonitoramentoEmpreendedorDTO);*/
}
