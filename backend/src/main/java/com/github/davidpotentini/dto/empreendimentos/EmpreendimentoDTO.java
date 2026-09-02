package com.github.davidpotentini.dto.empreendimentos;

import com.github.davidpotentini.enums.EEstagioIncubacao;
import com.github.davidpotentini.enums.ENivelMaturidade;
import com.github.davidpotentini.enums.ESituacaoContrato;
import com.github.davidpotentini.enums.EStatusEmpreendimento;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

/**
 * Empreendimento — DTO único de entrada e saída. {@code pessoas} é só de entrada na criação (pessoas
 * iniciais da startup, gravadas junto); nulo na leitura. O vínculo com o ciclo é gerido à parte
 * ({@code CICLO_EMPREENDIMENTOS}), fora deste DTO.
 */
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
        LocalDate entrada,
        LocalDate saida,
        List<PessoaEmpreendimentoDTO> pessoas
) {
}
