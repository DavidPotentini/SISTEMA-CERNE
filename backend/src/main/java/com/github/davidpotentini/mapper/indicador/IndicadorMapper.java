package com.github.davidpotentini.mapper.indicador;

import com.github.davidpotentini.dto.indicador.IndicadorCicloDTO;
import com.github.davidpotentini.model.indicador.IndicadorModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndicadorMapper {

    IndicadorCicloDTO toDTO(IndicadorModel indicador, String processoNome, String praticaNome,
                            String responsavelNome);

    @Mapping(target = "indCod", ignore = true)
    @Mapping(target = "origem", ignore = true)
    @Mapping(target = "cicCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    IndicadorModel toModel(IndicadorCicloDTO dto);
}
