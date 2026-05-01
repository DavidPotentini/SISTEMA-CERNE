package com.github.davidpotentini.cerne2.dto.informacoesgeraisincubadas.request;

import com.github.davidpotentini.cerne2.dto.endereco.request.EnderecoDTORequest;
import com.github.davidpotentini.cerne2.dto.endereco.response.EnderecoDTOResponse;
import com.github.davidpotentini.cerne2.enums.EStatusIncubacao;

import java.time.LocalDate;

public record IncubadasDTORequest(
        String nome,
        LocalDate dataInicioIncubacao,
        String email,
        EStatusIncubacao eStatusIncubacao,
        String descricao,
        String documentacao,
        EnderecoDTORequest enderecoDTORequest
) {
}
