package com.github.davidpotentini.cerne2.dto.formulario;

import java.util.Map;

public record FormularioRespostaDTO(Long frmCod,
                                    Long pessoaCod,
                                    Map<String, Object> resposta) {
}
