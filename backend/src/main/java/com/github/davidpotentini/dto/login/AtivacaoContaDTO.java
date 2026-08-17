package com.github.davidpotentini.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Ativação de conta na tela de login: o usuário informa o e-mail cadastrado pelo
 * administrador (conta {@code CONVIDADO}) e escolhe a própria senha. A conta passa a
 * {@code ATIVO}.
 */
public record AtivacaoContaDTO(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 6, message = "A senha deve ter ao menos 6 caracteres.") String senha
) {
}
