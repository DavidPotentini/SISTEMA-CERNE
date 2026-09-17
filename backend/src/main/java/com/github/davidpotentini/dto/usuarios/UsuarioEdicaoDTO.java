package com.github.davidpotentini.dto.usuarios;

import jakarta.validation.constraints.NotBlank;

public record UsuarioEdicaoDTO(
        @NotBlank String nome,
        Long incCod,
        Long papCod
) {
}
