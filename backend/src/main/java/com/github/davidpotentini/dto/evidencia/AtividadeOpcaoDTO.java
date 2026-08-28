package com.github.davidpotentini.dto.evidencia;

/**
 * Opção de atividade para o cadastro de evidência: cada atividade planejada do ciclo vigente já
 * trazendo sua prática e processo. A UI agrupa esta lista plana para montar os dropdowns em cascata
 * (processo → prática → atividade); a evidência guarda só o {@code atpCod}.
 */
public record AtividadeOpcaoDTO(
        Long atpCod,
        String nome,
        Long prtcCod,
        String praticaNome,
        Long prccCod,
        String processoNome) {
}
