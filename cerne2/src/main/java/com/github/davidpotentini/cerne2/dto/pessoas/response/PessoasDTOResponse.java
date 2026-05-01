package com.github.davidpotentini.cerne2.dto.pessoas.response;


import com.github.davidpotentini.cerne2.enums.ETipoEmpreendimento;

public record PessoasDTOResponse(
        Long pessoaCod,
        String nome,
        String email,
        String cpf,
        String cargo,
        Long incCod,
        ETipoEmpreendimento eTipoEmpreendimento
) {
}
