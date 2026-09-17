package com.github.davidpotentini.dto.planejamento;

import com.github.davidpotentini.enums.ENivelCerne;

import java.util.List;

public record PlanProcessoDTO(
        Long prccCod,
        ENivelCerne nivel,
        Integer ordem,
        String nome,
        String descricao,
        List<PlanPraticaDTO> praticas
) {
}
