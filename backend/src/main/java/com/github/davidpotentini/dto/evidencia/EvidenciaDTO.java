package com.github.davidpotentini.dto.evidencia;

import com.github.davidpotentini.enums.EStatusEvidencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EvidenciaDTO(
        Long evdCod,
        Integer evdCodSeq,
        @NotBlank String titulo,
        @NotNull Long atpCod,
        String atividadeNome,
        String processoNome,
        String praticaNome,
        Long arqCod,
        String arquivoNome,
        EStatusEvidencia status,
        Long regPesCod,
        String responsavel,
        LocalDateTime data,
        String motivoCorrecao) {
}
