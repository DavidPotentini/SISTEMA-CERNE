package com.github.davidpotentini.dto.modelos;

import java.util.List;

/**
 * Processo dentro da estrutura de um modelo — só leitura (herdado da metodologia, ordenado por
 * {@code ordem}), com suas práticas e atividades. Só entram processos ATIVOS da versão base.
 */
public record ModeloProcessoDTO(
        Long prcCod,
        Integer ordem,
        String nome,
        String descricao,
        List<ModeloPraticaDTO> praticas
) {
}
