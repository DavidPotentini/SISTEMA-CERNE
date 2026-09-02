package com.github.davidpotentini.dto.planejamento;

import com.github.davidpotentini.enums.EOrigemAtividade;
import com.github.davidpotentini.enums.EStatusAtividade;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * Atividade planejada — DTO único de entrada e saída. O vínculo ({@code plnCod}, {@code prtcCod}) e a
 * {@code origem} vêm da rota/geração; {@code status} é gerenciado na execução. Ao ajustar/incluir,
 * entram {@code nome}, {@code observacoes}, {@code respPesCod} e {@code prazo}. O
 * {@code responsavelNome} é rótulo de saída (nome do responsável), resolvido no service.
 */
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
        String responsavelNome
) {
}
