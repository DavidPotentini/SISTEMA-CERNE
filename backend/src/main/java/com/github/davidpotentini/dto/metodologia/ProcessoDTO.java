package com.github.davidpotentini.dto.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Processo — DTO único de entrada e saída. Na saída traz suas {@code praticas}; na criação a lista é
 * ignorada e {@code situacao} nasce {@code ATIVO}. {@code ordem} é gerida pelo backend (definida por
 * arrastar na tela); só leitura no DTO.
 */
public record ProcessoDTO(
        Long prcCod,
        Integer ordem,
        @NotBlank String nome,
        String descricao,
        EAtivoInativo situacao,
        List<PraticaDTO> praticas
) {
}
