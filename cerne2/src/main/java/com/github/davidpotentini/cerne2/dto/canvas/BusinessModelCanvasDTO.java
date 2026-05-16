package com.github.davidpotentini.cerne2.dto.canvas;

import java.util.Map;

public record BusinessModelCanvasDTO(Long bmcCod,
                                     Long ambcCod,
                                     Map<String, Object> parceirosChave,
                                     Map<String, Object> atividadesChave,
                                     Map<String, Object> propostasValor,
                                     Map<String, Object> relacionamentoCliente,
                                     Map<String, Object> segmentoCliente,
                                     Map<String, Object> canais,
                                     Map<String, Object> recursosChave,
                                     Map<String, Object> estruturaCustos,
                                     Map<String, Object> fontesReceitas) {
}
