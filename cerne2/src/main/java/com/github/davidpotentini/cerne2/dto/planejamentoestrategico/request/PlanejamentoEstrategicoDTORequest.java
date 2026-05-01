package com.github.davidpotentini.cerne2.dto.planejamentoestrategico.request;

import java.time.LocalDate;

public record PlanejamentoEstrategicoDTORequest(String nome,
                                                LocalDate dataInicio,
                                                LocalDate dataTermino) {

}
