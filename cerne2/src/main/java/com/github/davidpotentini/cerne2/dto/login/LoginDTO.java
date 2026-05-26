package com.github.davidpotentini.cerne2.dto.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Credenciais de login. {@code tntCod} é a empresa (tenant) selecionada pelo
 * usuário antes de informar e-mail e senha.
 */
public record LoginDTO(
        @NotNull Long tntCod,
        @NotBlank String email,
        @NotBlank String senha) {
}
