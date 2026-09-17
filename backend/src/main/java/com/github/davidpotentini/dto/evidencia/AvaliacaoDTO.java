package com.github.davidpotentini.dto.evidencia;

import com.github.davidpotentini.enums.EStatusEvidencia;
import jakarta.validation.constraints.NotNull;

public record AvaliacaoDTO(
        @NotNull EStatusEvidencia status,
        String motivo) {
}
