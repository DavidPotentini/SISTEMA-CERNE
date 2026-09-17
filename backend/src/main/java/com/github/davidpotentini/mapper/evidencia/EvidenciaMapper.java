package com.github.davidpotentini.mapper.evidencia;

import com.github.davidpotentini.dto.evidencia.EvidenciaDTO;
import com.github.davidpotentini.model.evidencia.EvidenciaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EvidenciaMapper {

    EvidenciaDTO toDTO(EvidenciaModel evidencia, String atividadeNome, String processoNome,
                       String praticaNome, String arquivoNome, String responsavel);

    @Mapping(target = "evdCod", ignore = true)
    @Mapping(target = "evdCodSeq", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "motivoCorrecao", ignore = true)
    @Mapping(target = "regPesCod", ignore = true)
    @Mapping(target = "data", ignore = true)
    @Mapping(target = "novo", ignore = true)
    EvidenciaModel toModel(EvidenciaDTO dto);
}
