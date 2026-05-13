package com.github.davidpotentini.cerne2.dto.canvas;

import java.util.Map;

public record ChannelImplementationCanvasDTO(Long ambcCod,
                                             Long segCod,
                                             Map<String, Object> atividadeConhecimento,
                                             Map<String, Object> atividadeAvaliacao,
                                             Map<String, Object> atividadeCompra,
                                             Map<String, Object> atividadeEntrega,
                                             Map<String, Object> atividadePosVenda,
                                             Map<String, Object> recursosConhecimento,
                                             Map<String, Object> recursosAvaliacao,
                                             Map<String, Object> recursosCompra,
                                             Map<String, Object> recursosEntrega,
                                             Map<String, Object> recursosPosVenda,
                                             Map<String, Object> parceirosConhecimento,
                                             Map<String, Object> parceirosAvaliacao,
                                             Map<String, Object> parceirosCompra,
                                             Map<String, Object> parceirosEntrega,
                                             Map<String, Object> parceirosPosVenda) {
}
