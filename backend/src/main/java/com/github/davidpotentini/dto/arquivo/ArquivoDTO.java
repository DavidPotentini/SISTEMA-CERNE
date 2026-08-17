package com.github.davidpotentini.dto.arquivo;

/**
 * Metadados de um arquivo enviado. {@code url} é uma URL de download temporária (presigned), montada
 * no service a cada leitura — não é persistida.
 */
public record ArquivoDTO(
        Long arqCod,
        String nomeOriginal,
        String contentType,
        Long tamanhoBytes,
        String url) {
}
