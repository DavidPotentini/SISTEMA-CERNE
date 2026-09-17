package com.github.davidpotentini.comum.tenant;

/** Identidade autenticada por requisição: principal do {@code SecurityContext} e fonte dos claims do JWT. */
public record UsuarioAutenticado(
        Long ctaCod,
        String email,
        String nomeSchema,
        Long papCod,
        String papelNome,
        boolean adminPlataforma
) {
}
