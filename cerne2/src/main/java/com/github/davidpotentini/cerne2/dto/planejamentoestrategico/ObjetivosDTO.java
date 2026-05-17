package com.github.davidpotentini.cerne2.dto.planejamentoestrategico;

import java.time.LocalDate;

public record ObjetivosDTO(Long objCod,
                           String nome,
                           LocalDate dataInicio,
                           LocalDate dataTermino,
                           Long prjCod) {
}
