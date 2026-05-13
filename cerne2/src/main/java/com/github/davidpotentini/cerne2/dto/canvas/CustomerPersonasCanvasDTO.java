package com.github.davidpotentini.cerne2.dto.canvas;

import java.util.Map;

public record CustomerPersonasCanvasDTO(Long ambcCod,
                                        Long personaCod,
                                        Map<String, Object> atributos) {
}
