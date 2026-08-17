package com.github.davidpotentini.dto.empreendimentos;

import com.github.davidpotentini.enums.EAtivoInativo;
import jakarta.validation.constraints.NotBlank;

/**
 * Pessoa de um empreendimento — DTO único de entrada e saída. {@code principal} = responsável
 * (contato principal). Na escrita, só {@code nome}/{@code papel}/{@code contato} são usados; a
 * pessoa nasce ATIVA e não-principal (o responsável é definido à parte).
 */
public record PessoaEmpreendimentoDTO(
        Long pseCod,
        Long empCod,
        @NotBlank String nome,
        String papel,
        boolean principal,
        String contato,
        EAtivoInativo situacao
) {
}
