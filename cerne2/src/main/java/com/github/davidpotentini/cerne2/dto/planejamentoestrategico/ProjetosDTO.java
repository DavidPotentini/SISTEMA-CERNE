package com.github.davidpotentini.cerne2.dto.planejamentoestrategico;

import java.time.LocalDate;

public record ProjetosDTO(Long prjCod,
                          String nome,
                          LocalDate dataInicio,
                          LocalDate dataTermino,
                          Long pesCod) {
}
