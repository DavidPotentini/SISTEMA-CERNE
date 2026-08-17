package com.github.davidpotentini.mapper.indicador;

import com.github.davidpotentini.dto.indicador.IndicadorCicloDTO;
import com.github.davidpotentini.model.indicador.IndicadorModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Conversão de indicadores do ciclo. Os rótulos do vínculo ({@code processoNome}/{@code praticaNome})
 * são resolvidos no service e passados prontos. Na volta ({@code toModel}) só entram os campos
 * editáveis; id, origem, ciclo e situação nascem no service.
 */
@Mapper(componentModel = "spring")
public interface IndicadorMapper {

    IndicadorCicloDTO toDTO(IndicadorModel indicador, String processoNome, String praticaNome);

    /** Definir complementar: só nome/prtCod/unidade/periodicidade; o resto é do service. */
    @Mapping(target = "indCod", ignore = true)
    @Mapping(target = "origem", ignore = true)
    @Mapping(target = "cicCod", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    IndicadorModel toModel(IndicadorCicloDTO dto);
}
