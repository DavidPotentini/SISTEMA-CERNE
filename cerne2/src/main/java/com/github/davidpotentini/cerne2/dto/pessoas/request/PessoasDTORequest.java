package com.github.davidpotentini.cerne2.dto.pessoas.request;

import com.github.davidpotentini.cerne2.enums.ETipoEmpreendimento;

public record PessoasDTORequest(
        String nome,
        String email,
        String cpf,
        String cargo,
        Long incCod,
        ETipoEmpreendimento eTipoEmpreendimento
) {
}
