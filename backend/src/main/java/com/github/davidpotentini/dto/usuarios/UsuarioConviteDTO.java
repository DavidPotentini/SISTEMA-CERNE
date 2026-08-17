package com.github.davidpotentini.dto.usuarios;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Corpo do convite de usuário (POST). A conta nasce {@code CONVIDADO} sem senha
 * utilizável. {@code incCod}/{@code papCod} são opcionais: quando informados, o admin já
 * vincula o usuário a uma incubadora e a um papel (linha em {@code PESSOAS} do tenant).
 */
public record UsuarioConviteDTO(
        @NotBlank String nome,
        @Email @NotBlank String email,
        Long incCod,
        Long papCod
) {
}
