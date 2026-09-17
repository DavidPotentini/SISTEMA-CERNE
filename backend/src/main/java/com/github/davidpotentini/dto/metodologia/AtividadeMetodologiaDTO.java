package com.github.davidpotentini.dto.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtividadeMetodologiaDTO(
        Long ameCod,
        @NotNull Long prtCod,
        Long agrCod,
        String vinculoMetodologico,
        @NotBlank String nome,
        String observacoes,
        boolean porEmpreendimento,
        EAtivoInativo situacao
) {
}
