package com.github.davidpotentini.comum.tenant;

/**
 * Identidade autenticada por requisição, no modelo novo. É o principal do
 * {@code SecurityContext} e a fonte dos claims do JWT (ver {@link JwtService}).
 *
 * <ul>
 *   <li>{@code ctaCod} / {@code email} — conta global em {@code public.CONTAS};</li>
 *   <li>{@code nomeSchema} — schema da incubadora (tenant) ativo na sessão;</li>
 *   <li>{@code papCod} / {@code papelNome} — papel local na incubadora (pode ser nulo).</li>
 * </ul>
 */
public record UsuarioAutenticado(
        Long ctaCod,
        String email,
        String nomeSchema,
        Long papCod,
        String papelNome,
        boolean adminPlataforma
) {
}
