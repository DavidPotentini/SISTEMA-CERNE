package com.github.davidpotentini.cerne2.dto.planejamentoestrategico.response;

import com.github.davidpotentini.cerne2.enums.ESituacaoTarefa;

import java.time.LocalDate;

public record TarefasDTOResponse(Long trfCod,
                                 String nome,
                                 LocalDate dataInicio,
                                 LocalDate dataTermino,
                                 ESituacaoTarefa eSituacaoTarefa,
                                 Long objCod,
                                 Long respCod) {
}
