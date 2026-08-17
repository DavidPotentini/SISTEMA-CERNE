package com.github.davidpotentini.comum.erro;

import com.github.davidpotentini.enums.ERecurso;

/** Papel sem permissão no recurso (ou sem papel) → 403. */
public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(ERecurso recurso) {
        super("Sem permissão para o recurso: " + recurso);
    }

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
