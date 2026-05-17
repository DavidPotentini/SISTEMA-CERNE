package com.github.davidpotentini.cerne2.dto.informacoesgeraisincubadas;

import com.github.davidpotentini.cerne2.dto.endereco.EnderecoDTO;
import com.github.davidpotentini.cerne2.enums.EStatusIncubacao;

import java.time.LocalDate;

public record IncubadasDTO(
        Long incCod,
        String nome,
        LocalDate dataInicioIncubacao,
        String email,
        EStatusIncubacao eStatusIncubacao,
        String descricao,
        String documentacao,
        EnderecoDTO enderecoDTO
) {
}
