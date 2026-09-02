package com.github.davidpotentini.dto.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Atividade-padrão da metodologia — DTO único de entrada e saída. Vincula-se a uma prática pelo
 * {@code prtCod} (o "Vínculo metodológico"); na leitura, {@code vinculoMetodologico} traz o nome dessa
 * prática — ignorado na escrita. "Quem"/"quando" não entram aqui (definidos só no planejamento).
 * {@code situacao} nasce {@code ATIVO}.
 */
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
