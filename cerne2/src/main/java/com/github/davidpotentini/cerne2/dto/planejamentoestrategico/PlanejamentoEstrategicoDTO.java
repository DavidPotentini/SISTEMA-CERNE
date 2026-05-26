package com.github.davidpotentini.cerne2.dto.planejamentoestrategico;

import com.github.davidpotentini.cerne2.enums.ETipoEmpreendimento;

import java.time.LocalDate;

public record PlanejamentoEstrategicoDTO(Long pesCod,
                                         String nome,
                                         LocalDate dataInicio,
                                         LocalDate dataTermino,
                                         ETipoEmpreendimento vldTipoEmpreendimento) {
}
