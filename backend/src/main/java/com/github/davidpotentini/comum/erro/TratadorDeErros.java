package com.github.davidpotentini.comum.erro;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.util.stream.Collectors.joining;

/**
 * Traduz as exceções de domínio em respostas HTTP padronizadas — tira {@code try/catch}
 * dos controllers e uniformiza o corpo do erro ({@link ErroResponse}).
 */
@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(NaoEncontradoException e) {
        return ResponseEntity.status(404).body(new ErroResponse(e.getMessage()));
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> regra(RegraNegocioException e) {
        return ResponseEntity.status(409).body(new ErroResponse(e.getMessage()));
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroResponse> acesso(AcessoNegadoException e) {
        return ResponseEntity.status(403).body(new ErroResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(joining("; "));
        return ResponseEntity.badRequest().body(new ErroResponse(msg));
    }
}
