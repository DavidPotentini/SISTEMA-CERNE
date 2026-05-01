package com.github.davidpotentini.cerne2.dto.servicosvaloragregado.request;

import com.github.davidpotentini.cerne2.enums.EAtivoInativo;

import java.math.BigDecimal;

public record ServicosValorAgregadoDTORequest(String servTitulo,
                                              String servDesc,
                                              BigDecimal servCusto,
                                              String servCondContratacao,
                                              String servAnexos) {



}
