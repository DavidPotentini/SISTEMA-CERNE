package com.github.davidpotentini.dto.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.EPeriodicidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Indicador da metodologia — DTO único de entrada e saída. Vincula-se a uma prática pelo
 * {@code prtCod} (o "Vínculo metodológico"); na leitura, {@code vinculoMetodologico} traz o nome
 * dessa prática — ignorado na escrita. {@code situacao} nasce {@code ATIVO}.
 */
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
