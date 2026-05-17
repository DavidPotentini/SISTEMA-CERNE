package com.github.davidpotentini.cerne2.dto.pessoas;

import com.github.davidpotentini.cerne2.enums.ETipoEmpreendimento;

public record PessoasDTO(
        Long pessoaCod,
        String nome,
        String email,
        String cpf,
        String cargo,
        Long incCod,
        ETipoEmpreendimento eTipoEmpreendimento
) {
}
