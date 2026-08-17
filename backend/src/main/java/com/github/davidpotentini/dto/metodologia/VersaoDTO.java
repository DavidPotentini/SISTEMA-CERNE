package com.github.davidpotentini.dto.metodologia;

import com.github.davidpotentini.enums.ESituacaoVersao;

import java.time.LocalDateTime;

/**
 * Versão da metodologia — DTO de saída. As abas usam o {@code verCod} do RASCUNHO (versão de
 * trabalho) e o flag {@code alterada} sinaliza mudanças não publicadas. No histórico,
 * {@code publicadoPor} traz o nome de quem publicou (derivado; nulo no rascunho).
 */
public record VersaoDTO(
        Long verCod,
        String versao,
        ESituacaoVersao situacao,
        boolean alterada,
        LocalDateTime publicadaEm,
        Long pubPesCod,
        String publicadoPor,
        String resumo
) {
}
