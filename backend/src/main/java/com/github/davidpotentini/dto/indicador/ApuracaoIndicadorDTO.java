package com.github.davidpotentini.dto.indicador;

import com.github.davidpotentini.enums.EPeriodicidade;

/**
 * Linha da tela de apuração: o indicador do ciclo com o resumo de apuração
 * ({@code apurados}/{@code totalPeriodos}). Sem períodos, a UI mostra "sem meta".
 */
public record ApuracaoIndicadorDTO(
        Long indCod,
        String nome,
        String processoNome,
        String praticaNome,
        EPeriodicidade periodicidade,
        String unidade,
        int totalPeriodos,
        int apurados) {
}
