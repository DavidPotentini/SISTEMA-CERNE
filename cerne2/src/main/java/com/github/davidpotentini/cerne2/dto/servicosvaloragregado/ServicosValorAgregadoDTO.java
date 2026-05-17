package com.github.davidpotentini.cerne2.dto.servicosvaloragregado;

import java.math.BigDecimal;

public record ServicosValorAgregadoDTO(Long servCod,
                                       String servTitulo,
                                       String servDesc,
                                       BigDecimal servCusto,
                                       String servCondContratacao,
                                       String servAnexos) {
}
