package com.github.davidpotentini.dto.painel;

import com.github.davidpotentini.enums.ETipoPendencia;

import java.time.LocalDate;

public record PendenciaDTO(
        ETipoPendencia tipo,
        Long referenciaId,
        String titulo,
        String processoNome,
        String praticaNome,
        String detalhe,
        LocalDate prazo,
        Long respPesCod
) {
}
