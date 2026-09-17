package com.github.davidpotentini.dto.indicador;

import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.EOrigemIndicador;
import com.github.davidpotentini.enums.EPeriodicidade;
import jakarta.validation.constraints.NotBlank;

public record IndicadorCicloDTO(
        Long indCod,
        @NotBlank String nome,
        EOrigemIndicador origem,
        Long prtcCod,
        String unidade,
        EPeriodicidade periodicidade,
        EAtivoInativo situacao,
        Long respPesCod,
        String processoNome,
        String praticaNome,
        String responsavelNome) {
}
