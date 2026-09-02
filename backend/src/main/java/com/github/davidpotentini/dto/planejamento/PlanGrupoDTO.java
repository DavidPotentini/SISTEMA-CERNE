package com.github.davidpotentini.dto.planejamento;

import java.util.List;

/**
 * Agrupamento (sub-plano) dentro da estrutura de um planejamento — nível entre a prática e as
 * atividades. {@code agrcCod} é a instância do agrupamento no ciclo ({@code AGRUPAMENTOS_CICLO}); é
 * {@code null} no grupo sintético "Sem agrupamento", que recolhe as atividades sem grupo (inclusive
 * complementares) para não sumirem da árvore.
 */
public record PlanGrupoDTO(
        Long agrcCod,
        String nome,
        Integer ordem,
        Long empCod,
        List<AtividadePlanejadaDTO> atividades
) {
}
