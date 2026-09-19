package com.github.davidpotentini.dto.empreendimentos;

import com.github.davidpotentini.enums.EEstagioIncubacao;
import com.github.davidpotentini.enums.ENivelMaturidade;
import com.github.davidpotentini.enums.ESituacaoContrato;
import com.github.davidpotentini.enums.EStatusEmpreendimento;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record EmpreendimentoDTO(
        Long empCod,
        @NotBlank String nome,
        String cnpj,
        String cnae,
        String atividadeEconomica,
        String instagram,
        String site,
        String email,
        ESituacaoContrato situacaoContrato,
        EEstagioIncubacao estagio,
        EStatusEmpreendimento status,
        ENivelMaturidade nivelMaturidade,
        LocalDate inicioContrato,
        LocalDate fimContrato,
        List<PessoaEmpreendimentoDTO> pessoas,
        Long cicCod
) {
}
