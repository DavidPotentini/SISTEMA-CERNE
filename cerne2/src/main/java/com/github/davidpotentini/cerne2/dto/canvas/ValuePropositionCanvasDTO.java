package com.github.davidpotentini.cerne2.dto.canvas;

import java.util.Map;

public record ValuePropositionCanvasDTO(Long ambcCod,
                                        Map<String, Object> criadoresGanho,
                                        Map<String, Object> produtosServicos,
                                        Map<String, Object> alivioDores,
                                        Map<String, Object> ganhos,
                                        Map<String, Object> dores,
                                        Map<String, Object> tarefasClientes) {
}
