package com.github.davidpotentini.dto.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.EPeriodicidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IndicadorDTO(
        Long inmCod,
        @NotNull Long prtCod,
        String vinculoMetodologico,
        @NotBlank String nome,
        String unidade,
        EPeriodicidade periodicidade,
        EAtivoInativo situacao
) {
}
