package com.github.davidpotentini.dto.equipe;

import com.github.davidpotentini.enums.EStatusConta;

public record PessoaEquipeDTO(
        Long ctaCod,
        String nome,
        String email,
        String papel,
        EStatusConta situacao
) {
}
