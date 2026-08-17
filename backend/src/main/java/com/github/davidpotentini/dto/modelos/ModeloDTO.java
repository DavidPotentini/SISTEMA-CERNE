package com.github.davidpotentini.dto.modelos;

import com.github.davidpotentini.enums.EPeriodicidade;
import com.github.davidpotentini.enums.EStatusModelo;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * Modelo de planejamento — DTO único de entrada e saída. Na criação, {@code verCod} é resolvido no
 * backend (última metodologia VIGENTE) e {@code status}/{@code publicadoEm} são gerenciados pelo
 * service; da metodologia base, {@code versaoMetodologia} é o rótulo (só leitura).
 */
public record ModeloDTO(
        Long modCod,
        Long verCod,
        String versaoMetodologia,
        @NotBlank String nome,
        EPeriodicidade periodicidade,
        EStatusModelo status,
        LocalDateTime publicadoEm,
        String descricao
) {
}
