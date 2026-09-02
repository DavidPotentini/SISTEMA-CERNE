package com.github.davidpotentini.dto.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.ENivelCerne;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Processo — DTO único de entrada e saída. Na saída traz suas {@code praticas}; na criação a lista é
 * ignorada e {@code situacao} nasce {@code ATIVO}. {@code ordem} é gerida pelo backend (definida por
 * arrastar na tela); só leitura no DTO. {@code nivel} é sempre {@code CERNE_1} por ora (só leitura;
 * o mapper preserva o valor do modelo).
 */
public record ProcessoDTO(
        Long prcCod,
        ENivelCerne nivel,
        Integer ordem,
        @NotBlank String nome,
        String descricao,
        EAtivoInativo situacao,
        List<PraticaDTO> praticas
) {
}
