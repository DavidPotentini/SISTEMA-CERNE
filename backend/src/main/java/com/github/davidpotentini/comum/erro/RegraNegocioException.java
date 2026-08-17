package com.github.davidpotentini.comum.erro;

/** Violação de regra de negócio (ex.: ciclo encerrado é só leitura) → 409. */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
