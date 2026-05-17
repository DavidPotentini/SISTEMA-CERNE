package com.github.davidpotentini.cerne2.dto.planejamentoestrategico;

import java.time.LocalDate;

public record PlanejamentoEstrategicoDTO(Long pesCod,
                                         String nome,
                                         LocalDate dataInicio,
                                         LocalDate dataTermino) {
}
