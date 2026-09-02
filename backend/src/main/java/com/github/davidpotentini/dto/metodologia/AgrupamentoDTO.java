package com.github.davidpotentini.dto.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
import jakarta.validation.constraints.NotBlank;

/**
 * Agrupamento (sub-plano) da metodologia — DTO único de entrada e saída. Vincula-se a uma prática pelo
 * {@code prtCod}; na criação ele vem do path (o service o define), por isso não é validado no corpo. Na
 * leitura, {@code vinculoMetodologico} traz o nome dessa prática — ignorado na escrita. {@code ordem} é
 * gerida pelo arrastar-e-soltar; {@code situacao} nasce {@code ATIVO}.
 */
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
