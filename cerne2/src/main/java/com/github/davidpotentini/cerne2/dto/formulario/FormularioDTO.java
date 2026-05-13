package com.github.davidpotentini.cerne2.dto.formulario;

import java.util.Map;

public record FormularioDTO(Long frmCod,
                            String nome,
                            String proposito,
                            Map<String, Object> jsonConfig) {
}
