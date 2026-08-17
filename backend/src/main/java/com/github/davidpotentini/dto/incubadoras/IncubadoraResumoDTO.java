package com.github.davidpotentini.dto.incubadoras;

import com.github.davidpotentini.enums.EStatusIncubadora;

/** Linha da listagem de incubadoras (colunas da tela do administrador). */
public record IncubadoraResumoDTO(
        Long incCod,
        String nome,
        String mantenedora,
        String responsavelNome,
        Long qtdUsuarios,
        EStatusIncubadora status
) {
}
