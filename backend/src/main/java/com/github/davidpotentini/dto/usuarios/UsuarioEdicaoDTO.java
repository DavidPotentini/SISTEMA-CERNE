package com.github.davidpotentini.dto.usuarios;

import jakarta.validation.constraints.NotBlank;

/**
 * Edição de um usuário existente (PUT): nome sempre; incubadora + papel opcionais (só
 * para contas de incubadora). O e-mail (identidade de login) não muda por aqui.
 */
public record UsuarioEdicaoDTO(
        @NotBlank String nome,
        Long incCod,
        Long papCod
) {
}
