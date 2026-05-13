package com.github.davidpotentini.cerne2.dto.validacaohipotese;

import java.util.List;

public record QuadroValidacaoHipoteseDTO(Long qvhCod,
                                         String tituloQuadro,
                                         Long incCod,
                                         List<HipoteseDTO> hipoteseDTOList) {
}
