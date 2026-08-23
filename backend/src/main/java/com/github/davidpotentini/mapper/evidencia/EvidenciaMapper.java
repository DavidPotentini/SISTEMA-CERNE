package com.github.davidpotentini.mapper.evidencia;

import com.github.davidpotentini.dto.evidencia.EvidenciaDTO;
import com.github.davidpotentini.model.evidencia.EvidenciaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Conversão de evidências. Os rótulos ({@code atividadeNome}, {@code processoNome}, {@code praticaNome},
 * {@code arquivoNome}, {@code responsavel}) são resolvidos no service e passados prontos; o
 * {@code motivoCorrecao} vem da própria versão. Na volta ({@code toModel}) só entram os campos
 * editáveis; chave, status, motivo, autor e data nascem no service.
 */
@Mapper(componentModel = "spring")
public interface EvidenciaMapper {

    EvidenciaDTO toDTO(EvidenciaModel evidencia, String atividadeNome, String processoNome,
                       String praticaNome, String arquivoNome, String responsavel);

    /** Registrar/corrigir: só título/atividade/arquivo; chave, status, motivo, autor e data são do service. */
    @Mapping(target = "evdCod", ignore = true)
    @Mapping(target = "evdCodSeq", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "motivoCorrecao", ignore = true)
    @Mapping(target = "regPesCod", ignore = true)
    @Mapping(target = "data", ignore = true)
    @Mapping(target = "novo", ignore = true)
    EvidenciaModel toModel(EvidenciaDTO dto);
}
