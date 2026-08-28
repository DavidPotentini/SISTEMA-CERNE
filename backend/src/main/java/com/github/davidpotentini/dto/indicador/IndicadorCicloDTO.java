package com.github.davidpotentini.dto.indicador;

import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.EOrigemIndicador;
import com.github.davidpotentini.enums.EPeriodicidade;
import jakarta.validation.constraints.NotBlank;

/**
 * Indicador de um ciclo (entrada + saída).
 *
 * <p><b>Entrada</b> (definir complementar): {@code nome}, {@code prtcCod} (vínculo CERNE opcional),
 * {@code unidade}, {@code periodicidade}, {@code respPesCod} (responsável — único campo editável nos
 * gerados da metodologia). <b>Saída</b>: acrescenta {@code indCod}, {@code origem}, {@code situacao},
 * os rótulos do "Vínculo CERNE" ({@code processoNome}/{@code praticaNome}) e o nome do responsável
 * ({@code responsavelNome}). A {@code origem} e o ciclo são definidos no service — não pelo cliente.
 */
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
