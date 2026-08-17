package com.github.davidpotentini.dto.monitoramento;

import com.github.davidpotentini.enums.EEixoCerne;

/** Nota de um eixo CERNE (0–5). Usado tanto na leitura da aplicação quanto na revisão. */
public record PontuacaoDTO(
        EEixoCerne dimensao,
        Integer pontuacao
) {
}
