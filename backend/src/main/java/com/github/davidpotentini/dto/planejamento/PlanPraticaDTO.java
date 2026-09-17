package com.github.davidpotentini.dto.planejamento;

import java.util.List;

public record PlanPraticaDTO(
        Long prtcCod,
        String nome,
        String descricao,
        List<PlanGrupoDTO> grupos
) {
}
