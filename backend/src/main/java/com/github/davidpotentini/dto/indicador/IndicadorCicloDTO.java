package com.github.davidpotentini.dto.indicador;

import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.EOrigemIndicador;
import com.github.davidpotentini.enums.EPeriodicidade;
import jakarta.validation.constraints.NotBlank;

/**
 * Indicador de um ciclo (entrada + saída).
 *
 * <p><b>Entrada</b> (definir complementar): {@code nome}, {@code prtCod} (vínculo CERNE opcional),
 * {@code unidade}, {@code periodicidade}. <b>Saída</b>: acrescenta {@code indCod}, {@code origem},
 * {@code situacao} e os rótulos do "Vínculo CERNE" ({@code processoNome}/{@code praticaNome}). A
 * {@code origem} e o ciclo são definidos no service — não pelo cliente.
 */
public record IndicadorCicloDTO(
        Long indCod,
        @NotBlank String nome,
        EOrigemIndicador origem,
        Long prtCod,
        String unidade,
        EPeriodicidade periodicidade,
        EAtivoInativo situacao,
        String processoNome,
        String praticaNome) {
}
