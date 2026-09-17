package com.github.davidpotentini.dto.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
import jakarta.validation.constraints.NotBlank;

public record PraticaDTO(
        Long prtCod,
        Long prcCod,
        @NotBlank String nome,
        String descricao,
        EAtivoInativo situacao
) {
}
