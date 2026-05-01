package com.github.davidpotentini.cerne2.dto.servicosvaloragregado.response;

import com.github.davidpotentini.cerne2.enums.EAtivoInativo;

import java.math.BigDecimal;

public record ServicosValorAgregadoDTOResponse(Long servCod,
                                               String servTitulo,
                                               String servDesc,
                                               BigDecimal servCusto,
                                               String servCondContratacao,
                                               String servAnexos) {



}
