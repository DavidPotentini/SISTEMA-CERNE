package com.github.davidpotentini.dto.painel;

import com.github.davidpotentini.enums.EEstadoProcesso;

public record ProcessoFluxoDTO(
        Long prccCod,
        String nome,
        int ordem,
        EEstadoProcesso estado,
        long atividadesConcluidas,
        long atividadesTotal) {
}
