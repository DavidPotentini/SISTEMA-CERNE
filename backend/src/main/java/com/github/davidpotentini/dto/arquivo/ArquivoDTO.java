package com.github.davidpotentini.dto.arquivo;

public record ArquivoDTO(
        Long arqCod,
        String nomeOriginal,
        String contentType,
        Long tamanhoBytes,
        String url) {
}
