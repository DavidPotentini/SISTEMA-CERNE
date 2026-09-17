package com.github.davidpotentini.dto.usuarios;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioConviteDTO(
        @NotBlank String nome,
        @Email @NotBlank String email,
        Long incCod,
        Long papCod
) {
}
