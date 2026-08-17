package com.github.davidpotentini.dto.ciclos;

import com.github.davidpotentini.enums.EStatusCiclo;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * Ciclo — DTO único de entrada e saída. Na criação, {@code status} e {@code emFoco} são ignorados:
 * um novo ciclo nasce {@code ATIVO} (e o ativo anterior é encerrado) e sem foco — o foco é definido
 * pelo botão "Pôr em foco".
 */
public record CicloDTO(
        Long cicCod,
        @NotBlank String nome,
        LocalDate inicio,
        LocalDate fim,
        EStatusCiclo status,
        boolean emFoco
) {
}
