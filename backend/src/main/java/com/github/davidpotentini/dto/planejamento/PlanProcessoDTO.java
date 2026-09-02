package com.github.davidpotentini.dto.planejamento;

import com.github.davidpotentini.enums.ENivelCerne;

import java.util.List;

/**
 * Processo dentro da estrutura de um planejamento — só leitura (herdado da metodologia base do
 * modelo, ordenado por {@code ordem}), com suas práticas e atividades. Só entram processos ATIVOS.
 * {@code nivel} é o nível CERNE do processo (por ora sempre {@code CERNE_1}).
 */
public record PlanProcessoDTO(
        Long prccCod,
        ENivelCerne nivel,
        Integer ordem,
        String nome,
        String descricao,
        List<PlanPraticaDTO> praticas
) {
}
