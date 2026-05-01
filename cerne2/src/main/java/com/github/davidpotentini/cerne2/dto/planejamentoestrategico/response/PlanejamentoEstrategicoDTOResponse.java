package com.github.davidpotentini.cerne2.dto.planejamentoestrategico.response;

import java.time.LocalDate;

public record PlanejamentoEstrategicoDTOResponse(Long pesCod,
                                                 String nome,
                                                 LocalDate dataInicio,
                                                 LocalDate dataTermino) {

}
