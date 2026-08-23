package com.github.davidpotentini.dto.indicador;

import com.github.davidpotentini.enums.EPeriodicidade;
import com.github.davidpotentini.enums.ESituacaoApuracao;

/**
 * Linha da tela de apuração: o indicador do ciclo com o resumo de apuração
 * ({@code apurados}/{@code totalPeriodos}) e a {@code situacao} (em aberto/atrasada/concluída,
 * derivada dos períodos). Sem períodos, {@code situacao} fica {@code null} e a UI mostra "sem meta".
 * {@code respPesCod} (responsável pela apuração) alimenta o filtro padrão da tela.
 */
public record ApuracaoIndicadorDTO(
        Long indCod,
        String nome,
        String processoNome,
        String praticaNome,
        EPeriodicidade periodicidade,
        String unidade,
        int totalPeriodos,
        int apurados,
        ESituacaoApuracao situacao,
        Long respPesCod) {
}
