package com.github.davidpotentini.dto.equipe;

import com.github.davidpotentini.enums.EStatusConta;

/**
 * Linha da "Equipe vinculada" da incubadora. {@code nome}/{@code email}/{@code situacao}
 * vêm de {@code public.CONTAS}; {@code papel} do {@code PAPEIS} do próprio tenant.
 */
public record PessoaEquipeDTO(
        Long ctaCod,
        String nome,
        String email,
        String papel,
        EStatusConta situacao
) {
}
