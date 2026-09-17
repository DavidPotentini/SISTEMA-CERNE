package com.github.davidpotentini.dto.ciclos;

import com.github.davidpotentini.enums.EStatusCiclo;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CicloDTO(
        Long cicCod,
        @NotBlank String nome,
        LocalDate inicio,
        LocalDate fim,
        EStatusCiclo status,
        boolean emFoco
) {
}
