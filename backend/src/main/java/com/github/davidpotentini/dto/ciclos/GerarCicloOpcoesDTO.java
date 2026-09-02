package com.github.davidpotentini.dto.ciclos;

import java.util.List;

/**
 * Opções do diálogo "Gerar do ciclo": as incubadas {@code ATIVO} ofertadas e as já {@code selecionadas}
 * no ciclo em foco (para pré-marcar na regeração).
 */
public record GerarCicloOpcoesDTO(
        List<EmpreendimentoOpcaoDTO> incubadas,
        List<Long> selecionadas
) {
}
