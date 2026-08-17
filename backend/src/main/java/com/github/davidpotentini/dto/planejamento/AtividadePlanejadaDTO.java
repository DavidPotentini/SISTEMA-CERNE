package com.github.davidpotentini.dto.planejamento;

import com.github.davidpotentini.enums.EOrigemAtividade;
import com.github.davidpotentini.enums.EStatusAtividade;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * Atividade planejada — DTO único de entrada e saída. O vínculo ({@code plnCod}, {@code prtCod}) e a
 * {@code origem} vêm da rota/geração; {@code status} é gerenciado na execução. Ao ajustar/incluir,
 * entram {@code nome}, {@code descricao}, {@code respPesCod} e {@code prazo}.
 */
public record AtividadePlanejadaDTO(
        Long atpCod,
        Long plnCod,
        EOrigemAtividade origem,
        Long prtCod,
        @NotBlank String nome,
        String descricao,
        Long respPesCod,
        LocalDate prazo,
        EStatusAtividade status,
        Long empCod
) {
}
