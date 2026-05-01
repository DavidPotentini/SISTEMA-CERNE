package com.github.davidpotentini.cerne2.dto.planejamentoestrategico.request;

import com.github.davidpotentini.cerne2.enums.ESituacaoTarefa;

import java.time.LocalDate;

public record TarefasDTORequest(String nome,
                                LocalDate dataInicio,
                                LocalDate dataTermino,
                                ESituacaoTarefa eSituacaoTarefa,
                                Long respCod) {
}