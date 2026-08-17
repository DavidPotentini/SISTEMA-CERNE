package com.github.davidpotentini.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Corpo do {@code POST /login} — apenas e-mail + senha. Não há seleção de empresa. */
public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String senha
) {
}
