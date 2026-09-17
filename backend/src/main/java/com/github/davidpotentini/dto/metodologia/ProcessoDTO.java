package com.github.davidpotentini.dto.metodologia;

import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.ENivelCerne;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

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
