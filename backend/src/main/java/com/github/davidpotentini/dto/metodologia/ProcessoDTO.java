package com.github.davidpotentini.dto.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Processo — DTO único de entrada e saída. Carrega o {@code verCod} da versão a que pertence
 * (informado pelo front). Na saída traz suas {@code praticas}; na criação a lista é ignorada e
 * {@code situacao} nasce {@code ATIVO}. {@code ordem} é informada pelo usuário e única na versão.
 */
public record ProcessoDTO(
        Long prcCod,
        @NotNull Long verCod,
        @NotNull Integer ordem,
        @NotBlank String nome,
        String descricao,
        EAtivoInativo situacao,
        List<PraticaDTO> praticas
) {
}
