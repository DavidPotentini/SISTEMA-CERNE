package com.github.davidpotentini.dto.planejamento;

import java.util.List;

/**
 * Processo dentro da estrutura de um planejamento — só leitura (herdado da metodologia base do
 * modelo, ordenado por {@code ordem}), com suas práticas e atividades. Só entram processos ATIVOS.
 */
public record PlanProcessoDTO(
        Long prcCod,
        Integer ordem,
        String nome,
        String descricao,
        List<PlanPraticaDTO> praticas
) {
}
