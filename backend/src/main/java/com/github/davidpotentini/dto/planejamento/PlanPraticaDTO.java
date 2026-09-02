package com.github.davidpotentini.dto.planejamento;

import java.util.List;

/**
 * Prática dentro da estrutura de um planejamento — só leitura (herdada da metodologia base do
 * modelo), acrescida dos {@code grupos} (sub-planos) com suas atividades planejadas. Só entram
 * práticas ATIVAS. As atividades sem grupo caem no grupo sintético "Sem agrupamento".
 */
public record PlanPraticaDTO(
        Long prtcCod,
        String nome,
        String descricao,
        List<PlanGrupoDTO> grupos
) {
}
