package com.github.davidpotentini.dto.incubadoras;

import com.github.davidpotentini.enums.EStatusIncubadora;

public record IncubadoraResumoDTO(
        Long incCod,
        String nome,
        String mantenedora,
        String responsavelNome,
        Long qtdUsuarios,
        EStatusIncubadora status
) {
}
