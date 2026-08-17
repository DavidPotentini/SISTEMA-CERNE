package com.github.davidpotentini.dto.planejamento;

import java.util.List;

/**
 * Prática dentro da estrutura de um planejamento — só leitura (herdada da metodologia base do
 * modelo), acrescida das {@code atividades} planejadas daquela prática. Só entram práticas ATIVAS.
 */
public record PlanPraticaDTO(
        Long prtCod,
        String nome,
        String descricao,
        List<AtividadePlanejadaDTO> atividades
) {
}
