package com.github.davidpotentini.dto.monitoramento;

import com.github.davidpotentini.enums.ESituacaoRodada;
import com.github.davidpotentini.enums.ETipoRodada;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

/**
 * Rodada de monitoramento — DTO único de entrada e saída. Na escrita, {@code empCods} traz os
 * empreendimentos participantes (gera {@code RODADA_INCUBADAS}); o ciclo é o ativo (resolvido no
 * service). Na leitura, {@code responsavelNome} traz o nome do responsável e {@code situacao} o
 * andamento — ambos ignorados na escrita.
 */
public record RodadaDTO(
        Long rodCod,
        @NotBlank String nome,
        ETipoRodada tipo,
        LocalDate prazo,
        Long respPesCod,
        String responsavelNome,
        ESituacaoRodada situacao,
        List<Long> empCods
) {
}
