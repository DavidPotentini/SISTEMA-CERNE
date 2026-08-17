package com.github.davidpotentini.dto.monitoramento;

import com.github.davidpotentini.enums.ERecomendacaoMonitor;
import com.github.davidpotentini.enums.EStatusMonitoramento;

import java.time.LocalDate;
import java.util.List;

/**
 * Aplicação da rodada a um empreendimento — DTO único de entrada e saída da avaliação. Card da aba
 * "Aplicações e pontuação": o empreendimento ({@code empCod}/{@code empNome}), sua avaliação por eixo
 * ({@code pontuacoes}), a recomendação do monitor e o status do monitoramento. Na leitura, quando
 * ainda não revisado, {@code avaCod}/{@code status} vêm nulos. Na revisão (escrita), valem
 * {@code status}, {@code recomendacao}, {@code observacao} e {@code pontuacoes}; o resto é derivado.
 */
public record AplicacaoDTO(
        Long avaCod,
        Long empCod,
        String empNome,
        EStatusMonitoramento status,
        LocalDate data,
        ERecomendacaoMonitor recomendacao,
        String observacao,
        List<PontuacaoDTO> pontuacoes
) {
}
