package com.github.davidpotentini.cerne2.dto.monitoramentoempreendedor;

public record PerguntasMonitoramentoEmpreendedorDTO (Long pergCod,
                                                     String descricaoPergunta,
                                                     Integer pontuacao,
                                                     String observacao,
                                                     Long fasCod) {
}
