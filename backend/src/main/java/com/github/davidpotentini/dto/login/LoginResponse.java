package com.github.davidpotentini.dto.login;

import com.github.davidpotentini.enums.ENivel;
import com.github.davidpotentini.enums.ERecurso;

import java.util.Map;

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
