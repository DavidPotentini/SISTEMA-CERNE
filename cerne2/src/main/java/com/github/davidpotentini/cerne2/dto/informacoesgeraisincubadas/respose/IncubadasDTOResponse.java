package com.github.davidpotentini.cerne2.dto.informacoesgeraisincubadas.respose;

import com.github.davidpotentini.cerne2.dto.endereco.response.EnderecoDTOResponse;
import com.github.davidpotentini.cerne2.enums.EStatusIncubacao;

import java.time.LocalDate;

public record IncubadasDTOResponse(
        Long incCod,
        String nome,
        LocalDate dataInicioIncubacao,
        String email,
        EStatusIncubacao eStatusIncubacao,
        String descricao,
        String documentacao,
        EnderecoDTOResponse enderecoDTOResponse
) {
}
