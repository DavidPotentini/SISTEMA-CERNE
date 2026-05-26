package com.github.davidpotentini.cerne2.dto.planejamentoestrategico;

import com.github.davidpotentini.cerne2.enums.ESituacaoTarefa;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalDate;

public record TarefasDTO(Long trfCod,
                         String nome,
                         LocalDate dataInicio,
                         LocalDate dataTermino,
                         ESituacaoTarefa eSituacaoTarefa,
                         Long objCod,
                         Long respCod,
                         Integer pontuacao,
                         String observacao) {
}
