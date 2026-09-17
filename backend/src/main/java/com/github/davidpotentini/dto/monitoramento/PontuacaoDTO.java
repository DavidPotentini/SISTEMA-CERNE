package com.github.davidpotentini.dto.monitoramento;

import com.github.davidpotentini.enums.EEixoCerne;

public record PontuacaoDTO(
        EEixoCerne dimensao,
        Integer pontuacao
) {
}
