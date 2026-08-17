package com.github.davidpotentini.dto.empreendimentos;

import com.github.davidpotentini.enums.EEstagioEmpreendimento;
import com.github.davidpotentini.enums.EModalidadeFisica;
import com.github.davidpotentini.enums.ESituacaoEmpreendimento;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * Empreendimento — DTO único de entrada e saída. {@code respPesCod} é o responsável interno (pessoa
 * da equipe da incubadora); na leitura, {@code responsavelNome} traz o nome dessa pessoa (de
 * {@code public.CONTAS}) — ignorado na escrita.
 */
public record EmpreendimentoDTO(
        Long empCod,
        @NotBlank String nome,
        String setor,
        EModalidadeFisica modalidadeFisica,
        EEstagioEmpreendimento estagio,
        ESituacaoEmpreendimento situacao,
        LocalDate entrada,
        Long respPesCod,
        String responsavelNome
) {
}
