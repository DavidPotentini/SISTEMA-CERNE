package com.github.davidpotentini.cerne2.dto.planejamentoestrategico.request;

import java.time.LocalDate;


public record ObjetivosDTORequest(String nome,
                                  LocalDate dataInicio,
                                  LocalDate dataTermino) {


}
