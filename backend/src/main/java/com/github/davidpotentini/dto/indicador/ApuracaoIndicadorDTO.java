package com.github.davidpotentini.dto.indicador;

import com.github.davidpotentini.enums.EPeriodicidade;
import com.github.davidpotentini.enums.ESituacaoApuracao;

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
