package com.github.davidpotentini.cerne2.dto.planejamentoestrategico.response;

import java.time.LocalDate;

public record ProjetosDTOResponse(Long prjCod,
                                  String nome,
                                  LocalDate dataInicio,
                                  LocalDate dataTermino,
                                  Long pesCod) {
}
