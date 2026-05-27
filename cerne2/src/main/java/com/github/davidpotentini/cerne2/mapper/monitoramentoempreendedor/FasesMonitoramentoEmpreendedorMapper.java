package com.github.davidpotentini.cerne2.mapper.monitoramentoempreendedor;

import com.github.davidpotentini.cerne2.dto.monitoramentoempreendedor.FasesMonitoramentoEmpreendedorDTO;
import com.github.davidpotentini.cerne2.mapper.metricas.IndicadoresMetricasMapper;
import com.github.davidpotentini.cerne2.models.monitoramentoempreendedor.FasesMonitoramentoEmpreendedorModel;
import com.github.davidpotentini.cerne2.repository.monitoramentoempreendedor.FasesMonitoramentoEmpreendedorRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = IndicadoresMetricasMapper.class)
public interface FasesMonitoramentoEmpreendedorMapper {

    /*@Mapping(source = "monitoramentoEmpreendedorModel.monCod", target = "monCod")
    FasesMonitoramentoEmpreendedorDTO toDTO(FasesMonitoramentoEmpreendedorModel fasesMonitoramentoEmpreendedorModel);

    List<FasesMonitoramentoEmpreendedorDTO> toDTOList(List<FasesMonitoramentoEmpreendedorModel> fasesMonitoramentoEmpreendedorModelList);

    @Mapping(source = "monCod", target = "monitoramentoEmpreendedorModel")
    @Mapping(target = "perguntasMonitoramentoEmpreendedorModelList", ignore = true)
    FasesMonitoramentoEmpreendedorModel toModel(FasesMonitoramentoEmpreendedorDTO fasesMonitoramentoEmpreendedorDTO);*/
}
