package com.github.davidpotentini.dto.ciclos;

import java.util.List;

public record GerarCicloOpcoesDTO(
        List<EmpreendimentoOpcaoDTO> incubadas,
        List<Long> selecionadas
) {
}
