package com.github.davidpotentini.dto.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
import jakarta.validation.constraints.NotBlank;

public record AgrupamentoDTO(
        Long agrCod,
        Long prtCod,
        String vinculoMetodologico,
        @NotBlank String nome,
        String descricao,
        Integer ordem,
        EAtivoInativo situacao
) {
}
