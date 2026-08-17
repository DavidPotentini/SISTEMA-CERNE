package com.github.davidpotentini.dto.evidencia;

import com.github.davidpotentini.enums.EStatusEvidencia;
import jakarta.validation.constraints.NotNull;

/**
 * Avaliação da versão corrente de uma evidência (acompanhamento de execução): o avaliador define o
 * {@code status} ({@code VALIDADA} ou {@code CORRECAO_SOLICITADA}) e, quando solicita correção, o
 * {@code motivo} — obrigatório nesse caso, ignorado ao validar.
 */
public record AvaliacaoDTO(
        @NotNull EStatusEvidencia status,
        String motivo) {
}
