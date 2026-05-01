package com.github.davidpotentini.cerne2.models.planejamentoestrategico.ids;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class AgrupamentoTarefaId {
    private Long agrCod;
    private Long trfCod;
}
