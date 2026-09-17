package com.github.davidpotentini.dto.usuarios;

import com.github.davidpotentini.enums.EStatusConta;

public record UsuarioResumoDTO(
        Long ctaCod,
        String nome,
        String email,
        Long incCod,
        String incubadora,
        Long papCod,
        String papel,
        EStatusConta status
) {
}
