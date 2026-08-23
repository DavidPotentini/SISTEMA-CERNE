package com.github.davidpotentini.dto.indicador;

import com.github.davidpotentini.enums.EPeriodicidade;

import java.math.BigDecimal;

/**
 * Linha do painel de indicadores do ciclo. Agrega todos os períodos do indicador: {@code metaTotal}
 * (soma das metas) e {@code atingidoTotal} (soma dos resultados registrados). {@code atingido} indica
 * meta batida ({@code atingidoTotal >= metaTotal}, com meta > 0); {@code pendente} indica ao menos um
 * período já encerrado (fim < hoje) sem resultado. Cards e consolidação por processo são derivados no
 * front a partir desta lista.
 */
public record PainelIndicadorDTO(
        Long indCod,
        String nome,
        String processoNome,
        String praticaNome,
        EPeriodicidade periodicidade,
        String unidade,
        boolean temMeta,
        BigDecimal metaTotal,
        BigDecimal atingidoTotal,
        boolean atingido,
        boolean pendente) {
}
