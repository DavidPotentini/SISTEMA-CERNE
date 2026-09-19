package com.github.davidpotentini.dto.empreendimentos;

import jakarta.validation.constraints.NotBlank;

public record PessoaEmpreendimentoDTO(
        Long pseCod,
        Long empCod,
        @NotBlank String nome,
        Boolean representanteLegal,
        String email,
        String telefone
) {
}
