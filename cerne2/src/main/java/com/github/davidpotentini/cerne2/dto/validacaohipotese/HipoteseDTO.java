package com.github.davidpotentini.cerne2.dto.validacaohipotese;

import com.github.davidpotentini.cerne2.enums.EBlocoLeanCanvas;
import com.github.davidpotentini.cerne2.enums.EDecisaoHipotese;
import com.github.davidpotentini.cerne2.enums.EResultadoHipotese;
import com.github.davidpotentini.cerne2.enums.EStatusHipotese;

public record HipoteseDTO(Long hipCod,
                          String tituloQuadro,
                          EBlocoLeanCanvas vldBlocoLeanCanvas,
                          String hipotese,
                          String experimento,
                          String metrica,
                          EStatusHipotese vldStatusHipotese,
                          EResultadoHipotese vldResultadoHipotese,
                          String resultadoDetalhamento,
                          EDecisaoHipotese vldDecisaoHipotese,
                          String decisaoDetalhamento,
                          String qvhCod) {
}
