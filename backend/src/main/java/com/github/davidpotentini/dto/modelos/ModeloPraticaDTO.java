package com.github.davidpotentini.dto.modelos;

import java.util.List;

/**
 * Prática dentro da estrutura de um modelo — só leitura (herdada da metodologia), acrescida das
 * {@code atividades} daquele modelo. Só entram práticas ATIVAS da versão base.
 */
public record ModeloPraticaDTO(
        Long prtCod,
        String nome,
        String descricao,
        List<AtividadeModeloDTO> atividades
) {
}
