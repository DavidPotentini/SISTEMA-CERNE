package com.github.davidpotentini.cerne2.dto.informacoesgeraisincubadas;

import com.github.davidpotentini.cerne2.dto.arquivo.ArquivoDTO;
import com.github.davidpotentini.cerne2.dto.endereco.EnderecoDTO;
import com.github.davidpotentini.cerne2.enums.EStatusIncubacao;

import java.time.LocalDate;
import java.util.List;

public record IncubadasDTO(
        Long incCod,
        String nome,
        LocalDate dataInicioIncubacao,
        String email,
        EStatusIncubacao eStatusIncubacao,
        String descricao,
//        String documentacao,
        EnderecoDTO enderecoDTO
) {
}
