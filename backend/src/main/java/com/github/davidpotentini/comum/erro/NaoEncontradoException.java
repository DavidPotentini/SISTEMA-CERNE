package com.github.davidpotentini.comum.erro;

/** Recurso inexistente → 404. */
public class NaoEncontradoException extends RuntimeException {
    public NaoEncontradoException(String recurso, Object id) {
        super(recurso + " não encontrado: " + id);
    }
}
