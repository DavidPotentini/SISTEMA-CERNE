package com.github.davidpotentini.dto.incubadoras;

import com.github.davidpotentini.enums.ENivelIncubadora;
import com.github.davidpotentini.enums.EStatusIncubadora;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record IncubadoraDTO(
        Long incCod,
        @NotBlank String nome,
        String cnpj,
        String mantenedora,
        Long respCtaCod,
        String responsavelNome,
        String email,
        String telefone,
        String cidade,
        ENivelIncubadora nivel,
        EStatusIncubadora status,
        String nomeSchema,
        LocalDateTime criadaEm,
        LocalDateTime ativadaEm
) {
}
