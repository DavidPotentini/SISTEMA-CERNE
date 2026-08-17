package com.github.davidpotentini.dto.indicador;

/**
 * Opção de vínculo CERNE (prática da metodologia vigente) para o cadastro de indicador complementar.
 * A UI agrupa por {@code prcCod}/{@code processoNome} para montar o seletor processo → prática.
 */
public record PraticaOpcaoDTO(
        Long prtCod,
        String praticaNome,
        Long prcCod,
        String processoNome) {
}
