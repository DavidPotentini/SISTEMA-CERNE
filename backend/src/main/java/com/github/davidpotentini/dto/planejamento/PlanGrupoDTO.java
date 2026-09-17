package com.github.davidpotentini.dto.planejamento;

import java.util.List;

public record PlanGrupoDTO(
        Long agrcCod,
        String nome,
        Integer ordem,
        Long empCod,
        List<AtividadePlanejadaDTO> atividades
) {
}
