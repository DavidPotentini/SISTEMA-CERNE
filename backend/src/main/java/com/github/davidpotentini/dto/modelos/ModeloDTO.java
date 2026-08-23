package com.github.davidpotentini.dto.modelos;

import com.github.davidpotentini.enums.EPeriodicidade;
import com.github.davidpotentini.enums.EStatusModelo;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * Modelo de planejamento — DTO único de entrada e saída. {@code status}/{@code publicadoEm} são
 * gerenciados pelo service; a estrutura de processos/práticas é herdada da metodologia da incubadora.
 */
public record ModeloDTO(
        Long modCod,
        @NotBlank String nome,
        EPeriodicidade periodicidade,
        EStatusModelo status,
        LocalDateTime publicadoEm,
        String descricao
) {
}
