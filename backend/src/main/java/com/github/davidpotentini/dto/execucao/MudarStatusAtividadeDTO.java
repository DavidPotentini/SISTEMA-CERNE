package com.github.davidpotentini.dto.execucao;

import com.github.davidpotentini.enums.EStatusAtividade;
import jakarta.validation.constraints.NotNull;

public record MudarStatusAtividadeDTO(
        @NotNull EStatusAtividade status) {
}
