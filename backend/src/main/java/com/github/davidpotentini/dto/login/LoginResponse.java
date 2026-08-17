package com.github.davidpotentini.dto.login;

import com.github.davidpotentini.enums.ENivel;
import com.github.davidpotentini.enums.ERecurso;

import java.util.Map;

/**
 * Contexto devolvido pelo login. Para o administrador da plataforma, {@code nomeSchema},
 * {@code papCod} e {@code papelNome} são nulos e {@code adminPlataforma} é {@code true}.
 */
public record LoginResponse(
        String token,
        Long ctaCod,
        String nome,
        String email,
        String nomeSchema,
        boolean adminPlataforma,
        Long papCod,
        String papelNome,
        Map<ERecurso, ENivel> permissoes
) {
}
