package com.github.davidpotentini.dto.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
import jakarta.validation.constraints.NotBlank;

/**
 * Prática — DTO único de entrada e saída. Na criação, {@code prtCod}/{@code prcCod}/{@code situacao}
 * vêm do contexto (o {@code prcCod} é o processo da URL) e nascem {@code ATIVO}.
 */
public record PraticaDTO(
        Long prtCod,
        Long prcCod,
        @NotBlank String nome,
        String descricao,
        EAtivoInativo situacao
) {
}
