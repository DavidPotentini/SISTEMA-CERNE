package com.github.davidpotentini.dto.planejamento;

import com.github.davidpotentini.enums.EOrigemAtividade;
import com.github.davidpotentini.enums.EStatusAtividade;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record AtividadePlanejadaDTO(
        Long atpCod,
        Long plnCod,
        EOrigemAtividade origem,
        Long prtcCod,
        Long agrcCod,
        @NotBlank String nome,
        String observacoes,
        Long respPesCod,
        LocalDate prazo,
        EStatusAtividade status,
        Long empCod,
        String empreendimentoNome,
        String responsavelNome
) {
}
