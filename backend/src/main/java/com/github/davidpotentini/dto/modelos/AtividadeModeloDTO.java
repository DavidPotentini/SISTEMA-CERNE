package com.github.davidpotentini.dto.modelos;

import com.github.davidpotentini.enums.EAtivoInativo;
import jakarta.validation.constraints.NotBlank;

/**
 * Atividade de um modelo — DTO único de entrada e saída. O vínculo ({@code modCod}, {@code prtCod})
 * vem da rota; na criação {@code situacao} nasce {@code ATIVO}. {@code respPesCod} é opcional.
 */
public record AtividadeModeloDTO(
        Long atmCod,
        Long modCod,
        Long prtCod,
        @NotBlank String nome,
        String descricao,
        Long respPesCod,
        EAtivoInativo situacao
) {
}
