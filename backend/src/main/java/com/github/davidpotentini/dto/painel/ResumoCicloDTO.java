package com.github.davidpotentini.dto.painel;

import java.util.List;

public record ResumoCicloDTO(
        String cicloNome,
        long atividadesConcluidas,
        long atividadesTotal,
        int progresso,
        long empreendimentosAtivos,
        long evidenciasRegistradas,
        long evidenciasValidadas,
        long indicadoresAtingidos,
        long indicadoresComMeta,
        long indicadoresTotal,
        List<ProcessoFluxoDTO> fluxo) {
}
