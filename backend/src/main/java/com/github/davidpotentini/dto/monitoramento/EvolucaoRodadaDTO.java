package com.github.davidpotentini.dto.monitoramento;

import java.time.LocalDate;
import java.util.List;

/**
 * Uma rodada em que o empreendimento foi avaliado — item da série do radar de evolução. Traz as
 * notas por eixo ({@code pontuacoes}) daquela rodada; a lista é ordenada cronologicamente (mais
 * antiga primeiro) para que a evolução seja lida na ordem das rodadas.
 */
public record EvolucaoRodadaDTO(
        Long rodCod,
        String rodadaNome,
        LocalDate data,
        List<PontuacaoDTO> pontuacoes
) {
}
