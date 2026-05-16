package com.github.davidpotentini.cerne2.dto.canvas;

import java.util.Map;

public record LeanCanvasDTO(Long leanCod,
                            Long ambcCod,
                            Map<String, Object> problema,
                            Map<String, Object> solucao,
                            Map<String, Object> metrica,
                            Map<String, Object> propostaValor,
                            Map<String, Object> competenciaEssencial,
                            Map<String, Object> canais,
                            Map<String, Object> segmentoCliente,
                            Map<String, Object> estruturaCustos,
                            Map<String, Object> modeloReceita) {
}
