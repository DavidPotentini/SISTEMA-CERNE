package com.github.davidpotentini.dto.empreendimentos;

import jakarta.validation.constraints.NotBlank;

/**
 * Pessoa de um empreendimento — DTO único de entrada e saída. {@code representanteLegal} = representante
 * legal perante a incubadora. Na escrita, só {@code nome}/{@code email}/{@code telefone} são usados; a
 * pessoa nasce não-representante (o representante legal é definido à parte).
 */
public record PessoaEmpreendimentoDTO(
        Long pseCod,
        Long empCod,
        @NotBlank String nome,
        boolean representanteLegal,
        String email,
        String telefone
) {
}
