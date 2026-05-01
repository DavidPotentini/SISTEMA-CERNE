package com.github.davidpotentini.cerne2.dto.planejamentoestrategico.response;

import java.time.LocalDate;


public record ObjetivosDTOResponse(Long objCod,
                                   String nome,
                                   LocalDate dataInicio,
                                   LocalDate dataTermino,
                                   Long prjCod) {


}
