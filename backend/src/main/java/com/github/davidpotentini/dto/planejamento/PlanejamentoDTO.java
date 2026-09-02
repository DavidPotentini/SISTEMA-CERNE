package com.github.davidpotentini.dto.planejamento;

import com.github.davidpotentini.enums.EStatusPlanejamento;

import java.time.LocalDate;

/**
 * Planejamento institucional — DTO único de saída. Reúne o cabeçalho ({@code nome}, ciclo, status,
 * período {@code inicio}/{@code fim}, responsável) e o resumo de execução usado no "Consultar
 * publicação": {@code totalAtividades}, {@code concluidas} e {@code progresso} (%). Os rótulos
 * ({@code cicloNome}, {@code responsavel}) e os derivados são montados no service.
 */
public record PlanejamentoDTO(
        Long plnCod,
        String nome,
        Long cicCod,
        String cicloNome,
        EStatusPlanejamento status,
        LocalDate inicio,
        LocalDate fim,
        Long respPesCod,
        String responsavel,
        int totalAtividades,
        int concluidas,
        int progresso
) {
}
